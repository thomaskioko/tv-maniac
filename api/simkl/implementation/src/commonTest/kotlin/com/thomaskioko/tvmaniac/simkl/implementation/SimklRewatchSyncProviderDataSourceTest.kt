package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchClose
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchSessionStatus
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchWrite
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchWriteResult
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAccount
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchEpisode
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryAdded
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryStatus
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryStatusResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchItemsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchSeason
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchShow
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowEntry
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowIds
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUser
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserSettingsResponse
import com.thomaskioko.tvmaniac.simkl.testing.FakeSimklRewatchRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.testing.FakeSimklUserRemoteDataSource
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Instant

internal class SimklRewatchSyncProviderDataSourceTest {

    private val rewatchRemoteDataSource = FakeSimklRewatchRemoteDataSource()
    private val userRemoteDataSource = FakeSimklUserRemoteDataSource()
    private val source = SimklRewatchSyncProviderDataSource(
        rewatchRemoteDataSource = rewatchRemoteDataSource,
        userRemoteDataSource = userRemoteDataSource,
    )

    @Test
    fun `should report simkl as its provider`() {
        source.provider shouldBe SyncProviderSource.SIMKL
    }

    @Test
    fun `should not send a write given the account tier is free`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Success(userSettings(type = "free")))

        val result = source.writeRewatch(write(providerSessionId = null))

        val success = result.shouldBeInstanceOf<ApiResponse.Success<RemoteRewatchWriteResult>>()
        rewatchRemoteDataSource.lastAddRewatchHistoryRequest.shouldBeNull()
        success.body.skipped shouldBe true
    }

    @Test
    fun `should not send a write given the account tier is unrecognised`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Success(userSettings(type = "premium_plus")))

        val result = source.writeRewatch(write(providerSessionId = null))

        val success = result.shouldBeInstanceOf<ApiResponse.Success<RemoteRewatchWriteResult>>()
        rewatchRemoteDataSource.lastAddRewatchHistoryRequest.shouldBeNull()
        success.body.skipped shouldBe true
    }

    @Test
    fun `should not send a write given the account tier is missing`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Success(userSettings(type = null)))

        val result = source.writeRewatch(write(providerSessionId = null))

        val success = result.shouldBeInstanceOf<ApiResponse.Success<RemoteRewatchWriteResult>>()
        rewatchRemoteDataSource.lastAddRewatchHistoryRequest.shouldBeNull()
        success.body.skipped shouldBe true
    }

    @Test
    fun `should send a write given the account tier is pro`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Success(userSettings(type = "pro")))

        source.writeRewatch(write(providerSessionId = null))

        rewatchRemoteDataSource.lastAddRewatchHistoryRequest.shouldNotBeNull()
    }

    @Test
    fun `should send a write given the account tier is vip`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Success(userSettings(type = "vip")))

        source.writeRewatch(write(providerSessionId = null))

        rewatchRemoteDataSource.lastAddRewatchHistoryRequest.shouldNotBeNull()
    }

    @Test
    fun `should report rewatch as unsupported given the user settings call fails`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom"))

        source.supportsRewatch() shouldBe false
    }

    @Test
    fun `should return the underlying error given the tier lookup fails`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom"))

        val result = source.writeRewatch(write(providerSessionId = null))

        result.shouldBeInstanceOf<ApiResponse.Error.HttpError<RemoteRewatchWriteResult>>()
        rewatchRemoteDataSource.lastAddRewatchHistoryRequest.shouldBeNull()
    }

    @Test
    fun `should not cache the tier given the lookup fails`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom"))
        source.writeRewatch(write(providerSessionId = null))
        userRemoteDataSource.setUserSettings(ApiResponse.Success(userSettings(type = "vip")))

        source.writeRewatch(write(providerSessionId = null))

        rewatchRemoteDataSource.lastAddRewatchHistoryRequest.shouldNotBeNull()
    }

    @Test
    fun `should send no provider session id given none was cached yet`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Success(userSettings(type = "vip")))

        source.writeRewatch(write(providerSessionId = null))

        val request = checkNotNull(rewatchRemoteDataSource.lastAddRewatchHistoryRequest)
        request.shows.first().rewatchId.shouldBeNull()
    }

    @Test
    fun `should forward the given provider session id as the rewatch id on the show`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Success(userSettings(type = "vip")))

        source.writeRewatch(write(providerSessionId = 7482L))

        val request = checkNotNull(rewatchRemoteDataSource.lastAddRewatchHistoryRequest)
        request.shows.first().rewatchId shouldBe 7482L
        request.shows.first().isRewatch shouldBe true
    }

    @Test
    fun `should return the rewatch id echoed back in the response`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Success(userSettings(type = "vip")))
        rewatchRemoteDataSource.setAddRewatchHistoryResponse(ApiResponse.Success(historyResponse(rewatchId = 7482L)))

        val result = source.writeRewatch(write(providerSessionId = null))

        val success = result.shouldBeInstanceOf<ApiResponse.Success<RemoteRewatchWriteResult>>()
        success.body.providerSessionId shouldBe 7482L
    }

    @Test
    fun `should return a null provider session id given the response carries no status`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Success(userSettings(type = "vip")))
        rewatchRemoteDataSource.setAddRewatchHistoryResponse(ApiResponse.Success(SimklRewatchHistoryResponse()))

        val result = source.writeRewatch(write(providerSessionId = null))

        val success = result.shouldBeInstanceOf<ApiResponse.Success<RemoteRewatchWriteResult>>()
        success.body.providerSessionId.shouldBeNull()
    }

    @Test
    fun `should treat a zero episode count as unknown given the response carries the sentinel`() = runTest {
        rewatchRemoteDataSource.setRewatchSessionsResponse(
            ApiResponse.Success(SimklRewatchItemsResponse(shows = listOf(rewatchShow(tmdbId = "1396", watchedEpisodesCount = 0)))),
        )

        val sessions = source.readRewatchSessions(providerShowId = 1396L).getOrThrow()

        sessions.first().episodeCount.shouldBeNull()
    }

    @Test
    fun `should treat a positive episode count as real given the extended payload is present`() = runTest {
        rewatchRemoteDataSource.setRewatchSessionsResponse(
            ApiResponse.Success(SimklRewatchItemsResponse(shows = listOf(rewatchShow(tmdbId = "1396", watchedEpisodesCount = 5)))),
        )

        val sessions = source.readRewatchSessions(providerShowId = 1396L).getOrThrow()

        sessions.first().episodeCount shouldBe 5L
    }

    @Test
    fun `should carry the provider rewatch id given sessions are read`() = runTest {
        rewatchRemoteDataSource.setRewatchSessionsResponse(
            ApiResponse.Success(
                SimklRewatchItemsResponse(shows = listOf(rewatchShow(tmdbId = "1396", watchedEpisodesCount = 5, rewatchId = 9001L))),
            ),
        )

        val sessions = source.readRewatchSessions(providerShowId = 1396L).getOrThrow()

        sessions.first().providerSessionId shouldBe 9001L
    }

    @Test
    fun `should ignore the canonical row given it carries is_rewatch false`() = runTest {
        rewatchRemoteDataSource.setRewatchSessionsResponse(
            ApiResponse.Success(
                SimklRewatchItemsResponse(
                    shows = listOf(
                        rewatchShow(tmdbId = "1396", watchedEpisodesCount = 5, isRewatch = false),
                        rewatchShow(tmdbId = "1396", watchedEpisodesCount = 3, isRewatch = true),
                    ),
                ),
            ),
        )

        val sessions = source.readRewatchSessions(providerShowId = 1396L).getOrThrow()

        sessions.size shouldBe 1
        sessions.first().episodeCount shouldBe 3L
    }

    @Test
    fun `should ignore a show whose tmdb id does not match`() = runTest {
        rewatchRemoteDataSource.setRewatchSessionsResponse(
            ApiResponse.Success(SimklRewatchItemsResponse(shows = listOf(rewatchShow(tmdbId = "1396", watchedEpisodesCount = 5)))),
        )

        val sessions = source.readRewatchSessions(providerShowId = 555L).getOrThrow()

        sessions shouldBe emptyList()
    }

    @Test
    fun `should send a close carrying the session id and the closed status given the account tier is vip`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Success(userSettings(type = "vip")))

        source.closeRewatch(RemoteRewatchClose(providerShowId = 1396L, providerSessionId = 7482L))

        val request = checkNotNull(rewatchRemoteDataSource.lastCloseRewatchSessionRequest)
        request.shows.first().rewatchId shouldBe 7482L
        request.shows.first().rewatchStatus shouldBe "closed"
        request.shows.first().isRewatch shouldBe true
        request.shows.first().ids.tmdb shouldBe "1396"
    }

    @Test
    fun `should not send a close given the account tier is free`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Success(userSettings(type = "free")))

        source.closeRewatch(RemoteRewatchClose(providerShowId = 1396L, providerSessionId = 7482L))

        rewatchRemoteDataSource.lastCloseRewatchSessionRequest.shouldBeNull()
    }

    @Test
    fun `should not send a close given the tier lookup fails`() = runTest {
        userRemoteDataSource.setUserSettings(ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom"))

        val result = source.closeRewatch(RemoteRewatchClose(providerShowId = 1396L, providerSessionId = 7482L))

        rewatchRemoteDataSource.lastCloseRewatchSessionRequest.shouldBeNull()
        result.shouldBeInstanceOf<ApiResponse.Error.HttpError<Unit>>()
    }

    @Test
    fun `should read the session status given sessions are read`() = runTest {
        rewatchRemoteDataSource.setRewatchSessionsResponse(
            ApiResponse.Success(
                SimklRewatchItemsResponse(
                    shows = listOf(rewatchShow(tmdbId = "1396", watchedEpisodesCount = 5, rewatchStatus = "completed")),
                ),
            ),
        )

        val sessions = source.readRewatchSessions(providerShowId = 1396L).getOrThrow()

        sessions.first().status shouldBe RemoteRewatchSessionStatus.COMPLETED
    }

    @Test
    fun `should take the earliest episode date as the start given the extended payload carries episodes`() = runTest {
        rewatchRemoteDataSource.setRewatchSessionsResponse(
            ApiResponse.Success(
                SimklRewatchItemsResponse(
                    shows = listOf(
                        rewatchShow(
                            tmdbId = "1396",
                            watchedEpisodesCount = 2,
                            seasons = listOf(
                                SimklRewatchSeason(
                                    number = 1,
                                    episodes = listOf(
                                        SimklRewatchEpisode(number = 2, watchedAt = "2026-02-01T00:00:00Z"),
                                        SimklRewatchEpisode(number = 1, watchedAt = "2026-01-15T00:00:00Z"),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val sessions = source.readRewatchSessions(providerShowId = 1396L).getOrThrow()

        sessions.first().startedAt shouldBe Instant.parse("2026-01-15T00:00:00Z").toEpochMilliseconds()
    }

    @Test
    fun `should report no start given the payload carries no episode dates`() = runTest {
        rewatchRemoteDataSource.setRewatchSessionsResponse(
            ApiResponse.Success(SimklRewatchItemsResponse(shows = listOf(rewatchShow(tmdbId = "1396", watchedEpisodesCount = 5)))),
        )

        val sessions = source.readRewatchSessions(providerShowId = 1396L).getOrThrow()

        sessions.first().startedAt.shouldBeNull()
    }

    private fun write(providerSessionId: Long?, episodeNumber: Long = 1L): RemoteRewatchWrite = RemoteRewatchWrite(
        localSessionId = 7L,
        providerShowId = 1396L,
        providerSessionId = providerSessionId,
        seasonNumber = 1L,
        episodeNumber = episodeNumber,
        watchedAt = 1_750_000_000_000L,
    )

    private fun userSettings(type: String?): SimklUserSettingsResponse = SimklUserSettingsResponse(
        user = SimklUser(name = "tester"),
        account = SimklAccount(id = 1L, type = type),
    )

    private fun historyResponse(rewatchId: Long?): SimklRewatchHistoryResponse = SimklRewatchHistoryResponse(
        added = SimklRewatchHistoryAdded(
            episodes = 1,
            statuses = listOf(
                SimklRewatchHistoryStatus(
                    response = SimklRewatchHistoryStatusResponse(rewatchId = rewatchId, rewatchStatus = "active"),
                ),
            ),
        ),
    )

    private fun rewatchShow(
        tmdbId: String,
        watchedEpisodesCount: Int?,
        isRewatch: Boolean = true,
        rewatchId: Long? = 1001L,
        rewatchStatus: String = "active",
        seasons: List<SimklRewatchSeason> = emptyList(),
    ): SimklRewatchShow = SimklRewatchShow(
        isRewatch = isRewatch,
        rewatchId = rewatchId,
        rewatchStatus = rewatchStatus,
        lastWatchedAt = "2026-01-01T00:00:00Z",
        watchedEpisodesCount = watchedEpisodesCount,
        seasons = seasons,
        show = SimklShowEntry(ids = SimklShowIds(simkl = 39687L, tmdb = tmdbId)),
    )
}
