package com.thomaskioko.tvmaniac.data.rewatch.implementation

import app.cash.sqldelight.db.QueryResult
import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchEpisode
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchSession
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchSessionStatus
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchWriteResult
import com.thomaskioko.tvmaniac.data.rewatch.testing.FakeRewatchSyncProviderDataSource
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.shows.testing.FakeTvShowsDao
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultRewatchRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val tvShowsDao = FakeTvShowsDao()
    private val syncObserver = FakeSyncObserver()
    private val dateTimeProvider = FakeDateTimeProvider()
    private val rewatchSyncProviderDataSource = FakeRewatchSyncProviderDataSource(provider = SyncProviderSource.TRAKT)
    private lateinit var rewatchSessionDao: DefaultRewatchSessionDao
    private var localShowId: Long = 0

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        rewatchSessionDao = DefaultRewatchSessionDao(database, dispatchers, dateTimeProvider)
        localShowId = addShow(tmdbId = TMDB_ID)
        addSeason(seasonId = SEASON_ID, showId = localShowId)
        addEpisode(episodeId = EPISODE_ID, seasonId = SEASON_ID, showId = localShowId, episodeNumber = 1L)
        addEpisode(episodeId = SECOND_EPISODE_ID, seasonId = SEASON_ID, showId = localShowId, episodeNumber = 2L)
        tvShowsDao.setLocalShowIdForTmdbId(tmdbId = TMDB_ID, showId = localShowId)
        tvShowsDao.setTmdbIdForLocalShowId(showId = localShowId, tmdbId = TMDB_ID)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should return a session id given a session is started for a known show`() = runTest {
        val repository = buildRepository()

        val sessionId = repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT)

        sessionId.shouldNotBeNull()
    }

    @Test
    fun `should return null given a session is started for an unknown show`() = runTest {
        val repository = buildRepository()

        val sessionId = repository.startSession(showId = UNKNOWN_TMDB_ID, startedAt = STARTED_AT)

        sessionId.shouldBeNull()
    }

    @Test
    fun `should emit the started session given sessions for the show are observed`() = runTest {
        val repository = buildRepository()

        val sessionId = repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT)

        repository.observeSessionsForShow(TMDB_ID).test {
            val sessions = awaitItem()
            sessions.size shouldBe 1
            sessions.first().id shouldBe sessionId
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit an empty list given sessions for an unknown show are observed`() = runTest {
        val repository = buildRepository()

        repository.observeSessionsForShow(UNKNOWN_TMDB_ID).test {
            awaitItem().shouldBeEmpty()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should set the closed timestamp given the session is closed`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))

        repository.closeSession(sessionId = sessionId, closedAt = CLOSED_AT)

        repository.observeSessionsForShow(TMDB_ID).test {
            awaitItem().first().closedAt shouldBe CLOSED_AT
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return one plus session rows given an episode was added to a session`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))

        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )

        repository.observeEpisodeRewatches(EPISODE_ID).first() shouldBe 1L
    }

    @Test
    fun `should return the open session given one exists for the show`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))

        repository.openSessionForShow(TMDB_ID)?.id shouldBe sessionId
    }

    @Test
    fun `should return null given no open session exists for the show`() = runTest {
        val repository = buildRepository()

        repository.openSessionForShow(TMDB_ID).shouldBeNull()
    }

    @Test
    fun `should upload the episode to the active provider given it is added to a session`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))

        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 3L,
            watchedAt = REWATCHED_AT,
        )

        val write = checkNotNull(rewatchSyncProviderDataSource.lastWrite)
        write.localSessionId shouldBe sessionId
        write.providerShowId shouldBe TMDB_ID
        write.providerSessionId.shouldBeNull()
        write.seasonNumber shouldBe 1L
        write.episodeNumber shouldBe 3L
        write.watchedAt shouldBe REWATCHED_AT
    }

    @Test
    fun `should save the provider session id returned by the first upload`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        rewatchSyncProviderDataSource.setWriteRewatchResponse(
            ApiResponse.Success(RemoteRewatchWriteResult(providerSessionId = PROVIDER_SESSION_ID)),
        )

        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )

        repository.openSessionForShow(TMDB_ID)?.providerSessionId shouldBe PROVIDER_SESSION_ID
    }

    @Test
    fun `should send the saved provider session id with the next upload for the same session`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        rewatchSyncProviderDataSource.setWriteRewatchResponse(
            ApiResponse.Success(RemoteRewatchWriteResult(providerSessionId = PROVIDER_SESSION_ID)),
        )
        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )

        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 2L,
            watchedAt = SECOND_REWATCHED_AT,
        )

        val secondWrite = checkNotNull(rewatchSyncProviderDataSource.lastWrite)
        secondWrite.providerSessionId shouldBe PROVIDER_SESSION_ID
    }

    @Test
    fun `should leave the episode unsent given the upload fails`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        rewatchSyncProviderDataSource.setWriteRewatchResponse(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "offline"),
        )

        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )

        rewatchSessionDao.unsentEpisodes().map { it.episodeNumber } shouldBe listOf(1L)
    }

    @Test
    fun `should resend an unsent episode given pending rewatches are synced`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        rewatchSyncProviderDataSource.setWriteRewatchResponse(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "offline"),
        )
        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )
        rewatchSyncProviderDataSource.setWriteRewatchResponse(ApiResponse.Success(RemoteRewatchWriteResult()))

        repository.syncPendingRewatches()

        val write = checkNotNull(rewatchSyncProviderDataSource.lastWrite)
        write.localSessionId shouldBe sessionId
        write.seasonNumber shouldBe 1L
        write.episodeNumber shouldBe 1L
        rewatchSessionDao.unsentEpisodes().shouldBeEmpty()
    }

    @Test
    fun `should mark the episode synced given the upload succeeds`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))

        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )

        rewatchSessionDao.unsentEpisodes().shouldBeEmpty()
    }

    @Test
    fun `should not upload an episode again given its upload already succeeded and pending rewatches are synced`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )
        rewatchSessionDao.unsentEpisodes().shouldBeEmpty()
        rewatchSyncProviderDataSource.setWriteRewatchResponse(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "should not be sent again"),
        )

        syncObserver.errors.test {
            repository.syncPendingRewatches()
            expectNoEvents()
        }
    }

    @Test
    fun `should leave the episode unsent given the upload is skipped for a free account`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        rewatchSyncProviderDataSource.setWriteRewatchResponse(ApiResponse.Success(RemoteRewatchWriteResult(skipped = true)))

        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )

        rewatchSessionDao.unsentEpisodes().map { it.episodeNumber } shouldBe listOf(1L)
    }

    @Test
    fun `should not drain given the active source does not support rewatch`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        rewatchSyncProviderDataSource.setWriteRewatchResponse(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "offline"),
        )
        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )
        rewatchSyncProviderDataSource.setSupportsRewatch(false)
        rewatchSyncProviderDataSource.setWriteRewatchException(IllegalStateException("should not be called"))

        repository.syncPendingRewatches()

        rewatchSessionDao.unsentEpisodes().map { it.episodeNumber } shouldBe listOf(1L)
    }

    @Test
    fun `should attempt the second row given the first row in a drain fails`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        rewatchSyncProviderDataSource.setWriteRewatchResponse(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "offline"),
        )
        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )
        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = SECOND_EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 2L,
            watchedAt = SECOND_REWATCHED_AT,
        )
        rewatchSyncProviderDataSource.setWriteRewatchHandler { write ->
            if (write.episodeNumber == 1L) {
                ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "still offline")
            } else {
                ApiResponse.Success(RemoteRewatchWriteResult())
            }
        }

        repository.syncPendingRewatches()

        rewatchSessionDao.unsentEpisodes().map { it.episodeNumber } shouldBe listOf(1L)
    }

    @Test
    fun `should pin the id from the first successful row onto the second row in the same drain`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        rewatchSyncProviderDataSource.setWriteRewatchResponse(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "offline"),
        )
        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )
        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = SECOND_EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 2L,
            watchedAt = SECOND_REWATCHED_AT,
        )
        rewatchSyncProviderDataSource.setWriteRewatchHandler { write ->
            if (write.episodeNumber == 1L) {
                ApiResponse.Success(RemoteRewatchWriteResult(providerSessionId = PROVIDER_SESSION_ID))
            } else {
                ApiResponse.Success(RemoteRewatchWriteResult(providerSessionId = write.providerSessionId))
            }
        }

        repository.syncPendingRewatches()

        val secondRowWrite = checkNotNull(rewatchSyncProviderDataSource.lastWrite)
        secondRowWrite.episodeNumber shouldBe 2L
        secondRowWrite.providerSessionId shouldBe PROVIDER_SESSION_ID
        rewatchSessionDao.unsentEpisodes().shouldBeEmpty()
    }

    @Test
    fun `should not upload the episode given no provider is active`() = runTest {
        val repository = buildRepository(activeSource = { null })
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))

        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )

        rewatchSyncProviderDataSource.lastWrite.shouldBeNull()
    }

    @Test
    fun `should log a sync error given the upload to the active provider fails`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        rewatchSyncProviderDataSource.setWriteRewatchResponse(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom"),
        )

        syncObserver.errors.test {
            repository.addEpisodeToSession(
                sessionId = sessionId,
                showId = TMDB_ID,
                episodeId = EPISODE_ID,
                seasonNumber = 1L,
                episodeNumber = 1L,
                watchedAt = REWATCHED_AT,
            )

            awaitItem()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should throw the cancellation given the upload to the active provider is cancelled`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        rewatchSyncProviderDataSource.setWriteRewatchException(CancellationException("cancelled"))

        shouldThrow<CancellationException> {
            repository.addEpisodeToSession(
                sessionId = sessionId,
                showId = TMDB_ID,
                episodeId = EPISODE_ID,
                seasonNumber = 1L,
                episodeNumber = 1L,
                watchedAt = REWATCHED_AT,
            )
        }
    }

    @Test
    fun `should count an unsent repeat viewing given the active source supports rewatch`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        rewatchSessionDao.addEpisodeToSession(sessionId = sessionId, episodeId = EPISODE_ID, watchedAt = REWATCHED_AT)

        repository.countPendingRewatches() shouldBe 1L
    }

    @Test
    fun `should not count a repeat viewing given it was sent`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )

        repository.countPendingRewatches() shouldBe 0L
    }

    @Test
    fun `should not count an unsent repeat viewing given the active source does not support rewatch`() = runTest {
        val repository = buildRepository()
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        rewatchSessionDao.addEpisodeToSession(sessionId = sessionId, episodeId = EPISODE_ID, watchedAt = REWATCHED_AT)
        rewatchSyncProviderDataSource.setSupportsRewatch(false)

        repository.countPendingRewatches() shouldBe 0L
    }

    @Test
    fun `should not count an unsent repeat viewing given no provider is active`() = runTest {
        val repository = buildRepository(activeSource = { null })
        val sessionId = checkNotNull(repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT))
        rewatchSessionDao.addEpisodeToSession(sessionId = sessionId, episodeId = EPISODE_ID, watchedAt = REWATCHED_AT)

        repository.countPendingRewatches() shouldBe 0L
    }

    @Test
    fun `should return true given supportsRewatch is called with no active provider`() = runTest {
        val repository = buildRepository(activeSource = { null })

        repository.supportsRewatch() shouldBe true
    }

    @Test
    fun `should delegate to the active source given supportsRewatch is called`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setSupportsRewatch(false)

        repository.supportsRewatch() shouldBe false
    }

    @Test
    fun `should restore the viewings of every reported show given the account is synced`() = runTest {
        val repository = buildRepository()
        val otherLocalShowId = addShow(tmdbId = OTHER_TMDB_ID)
        tvShowsDao.setLocalShowIdForTmdbId(tmdbId = OTHER_TMDB_ID, showId = otherLocalShowId)
        addSeason(seasonId = OTHER_SEASON_ID, showId = otherLocalShowId)
        addEpisode(episodeId = OTHER_EPISODE_ID, seasonId = OTHER_SEASON_ID, showId = otherLocalShowId, episodeNumber = 1L)
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            ApiResponse.Success(
                mapOf(
                    TMDB_ID to listOf(remoteSession(providerSessionId = PROVIDER_SESSION_ID, episodes = listOf(remoteEpisode(viewings = 2L)))),
                    OTHER_TMDB_ID to listOf(
                        remoteSession(providerSessionId = OTHER_PROVIDER_SESSION_ID, episodes = listOf(remoteEpisode(viewings = 3L))),
                    ),
                ),
            ),
        )

        repository.syncRewatchSessions()

        rewatchSessionDao.observeEpisodeRewatches(EPISODE_ID).first() shouldBe 2L
        rewatchSessionDao.observeEpisodeRewatches(OTHER_EPISODE_ID).first() shouldBe 3L
    }

    @Test
    fun `should write nothing given no active provider`() = runTest {
        val repository = buildRepository(activeSource = { null })

        repository.syncRewatchSessions()

        rewatchSessionDao.observeSessionsForShow(localShowId).first().shouldBeEmpty()
    }

    @Test
    fun `should log a sync error given the provider returns an error`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom"),
        )

        syncObserver.errors.test {
            repository.syncRewatchSessions()

            awaitItem()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should throw the cancellation given the account sync is cancelled`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsException(CancellationException("cancelled"))

        shouldThrow<CancellationException> {
            repository.syncRewatchSessions()
        }
    }

    @Test
    fun `should close the session given the rewatch covers every aired episode`() = runTest {
        val repository = buildRepository()
        val sessionId = repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT)!!

        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )
        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = SECOND_EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 2L,
            watchedAt = SECOND_REWATCHED_AT,
        )

        rewatchSessionDao.sessionById(sessionId)?.closedAt shouldBe SECOND_REWATCHED_AT
    }

    @Test
    fun `should leave the session open given the rewatch covers only some aired episodes`() = runTest {
        val repository = buildRepository()
        val sessionId = repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT)!!

        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )

        rewatchSessionDao.sessionById(sessionId)?.closedAt.shouldBeNull()
    }

    @Test
    fun `should send a close to the active source given a session with a provider id is finished`() = runTest {
        val repository = buildRepository()
        val sessionId = repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT)!!
        rewatchSessionDao.setProviderSessionId(sessionId = sessionId, providerSessionId = PROVIDER_SESSION_ID)

        repository.finishSession(sessionId = sessionId, closedAt = CLOSED_AT)

        rewatchSyncProviderDataSource.lastClose.shouldNotBeNull().providerSessionId shouldBe PROVIDER_SESSION_ID
        rewatchSessionDao.sessionById(sessionId)?.closedAt shouldBe CLOSED_AT
    }

    @Test
    fun `should not send a close given the finished session has no provider id`() = runTest {
        val repository = buildRepository()
        val sessionId = repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT)!!

        repository.finishSession(sessionId = sessionId, closedAt = CLOSED_AT)

        rewatchSyncProviderDataSource.lastClose.shouldBeNull()
        rewatchSessionDao.sessionById(sessionId)?.closedAt shouldBe CLOSED_AT
    }

    @Test
    fun `should not send a close given the session was closed by covering the show`() = runTest {
        val repository = buildRepository()
        val sessionId = repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT)!!
        rewatchSessionDao.setProviderSessionId(sessionId = sessionId, providerSessionId = PROVIDER_SESSION_ID)

        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )
        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = SECOND_EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 2L,
            watchedAt = SECOND_REWATCHED_AT,
        )

        rewatchSyncProviderDataSource.lastClose.shouldBeNull()
    }

    @Test
    fun `should log a sync error given the close to the active source fails`() = runTest {
        val repository = buildRepository()
        val sessionId = repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT)!!
        rewatchSessionDao.setProviderSessionId(sessionId = sessionId, providerSessionId = PROVIDER_SESSION_ID)
        rewatchSyncProviderDataSource.setCloseRewatchResponse(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom"),
        )

        syncObserver.errors.test {
            repository.finishSession(sessionId = sessionId, closedAt = CLOSED_AT)

            awaitItem()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should write a local session given the provider reports one carrying a session id`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(listOf(remoteSession(status = RemoteRewatchSessionStatus.CLOSED))),
        )

        repository.syncRewatchSessions()

        val stored = rewatchSessionDao.observeSessionsForShow(localShowId).first()
        stored.single().providerSessionId shouldBe PROVIDER_SESSION_ID
        stored.single().startedAt shouldBe STARTED_AT
        stored.single().closedAt shouldBe REWATCHED_AT
    }

    @Test
    fun `should keep one local session given the same provider session syncs twice`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(listOf(remoteSession(status = RemoteRewatchSessionStatus.CLOSED))),
        )

        repository.syncRewatchSessions()
        repository.syncRewatchSessions()

        rewatchSessionDao.observeSessionsForShow(localShowId).first().size shouldBe 1
    }

    @Test
    fun `should leave a backfilled session open given the provider reports it active`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(listOf(remoteSession(status = RemoteRewatchSessionStatus.ACTIVE))),
        )

        repository.syncRewatchSessions()

        rewatchSessionDao.observeSessionsForShow(localShowId).first().single().closedAt.shouldBeNull()
    }

    @Test
    fun `should write one local session given the provider reports one without a session id`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(listOf(remoteSession(providerSessionId = null, episodes = listOf(remoteEpisode())))),
        )

        repository.syncRewatchSessions()
        repository.syncRewatchSessions()

        rewatchSessionDao.observeSessionsForShow(localShowId).first().size shouldBe 1
    }

    @Test
    fun `should close a restored session given the provider reports no session id`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(
                listOf(
                    remoteSession(
                        providerSessionId = null,
                        status = RemoteRewatchSessionStatus.ACTIVE,
                        episodes = listOf(remoteEpisode()),
                    ),
                ),
            ),
        )

        repository.syncRewatchSessions()

        rewatchSessionDao.openSessionForShow(localShowId).shouldBeNull()
    }

    @Test
    fun `should restore the viewings of an episode given the provider reports them`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(listOf(remoteSession(episodes = listOf(remoteEpisode(viewings = 2L))))),
        )

        repository.syncRewatchSessions()

        rewatchSessionDao.observeEpisodeRewatches(EPISODE_ID).first() shouldBe 2L
    }

    @Test
    fun `should keep the restored viewings unchanged given the same sync runs twice`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(listOf(remoteSession(episodes = listOf(remoteEpisode(viewings = 2L))))),
        )

        repository.syncRewatchSessions()
        repository.syncRewatchSessions()

        rewatchSessionDao.observeEpisodeRewatches(EPISODE_ID).first() shouldBe 2L
    }

    @Test
    fun `should add every reported viewing given two sessions carry the same episode`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(
                listOf(
                    remoteSession(providerSessionId = PROVIDER_SESSION_ID, episodes = listOf(remoteEpisode())),
                    remoteSession(providerSessionId = OTHER_PROVIDER_SESSION_ID, episodes = listOf(remoteEpisode())),
                ),
            ),
        )

        repository.syncRewatchSessions()

        rewatchSessionDao.observeEpisodeRewatches(EPISODE_ID).first() shouldBe 2L
    }

    @Test
    fun `should keep a locally recorded viewing given the provider reports the same one`() = runTest {
        val repository = buildRepository()
        val sessionId = repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT)!!
        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = REWATCHED_AT,
        )
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(listOf(remoteSession(episodes = listOf(remoteEpisode())))),
        )

        repository.syncRewatchSessions()

        rewatchSessionDao.observeEpisodeRewatches(EPISODE_ID).first() shouldBe 1L
    }

    @Test
    fun `should send no restored viewing back to the provider given pending rewatches are synced`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(listOf(remoteSession(episodes = listOf(remoteEpisode(viewings = 2L))))),
        )
        repository.syncRewatchSessions()

        repository.syncPendingRewatches()

        rewatchSyncProviderDataSource.lastWrite.shouldBeNull()
    }

    @Test
    fun `should still write the session given the episode is not stored yet`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(listOf(remoteSession(episodes = listOf(remoteEpisode(episodeNumber = 99L))))),
        )

        repository.syncRewatchSessions()

        rewatchSessionDao.observeSessionsForShow(localShowId).first().size shouldBe 1
    }

    @Test
    fun `should restore the viewings of an episode given it is stored after the sync`() = runTest {
        val repository = buildRepository()
        reportLateEpisode(repository, viewings = 2L)

        storeLateEpisode()

        rewatchSessionDao.observeEpisodeRewatches(LATE_EPISODE_ID).first() shouldBe 2L
    }

    @Test
    fun `should restore each viewing once given three viewings are reported before the episode is stored`() = runTest {
        val repository = buildRepository()
        reportLateEpisode(repository, viewings = 3L)

        storeLateEpisode()

        rewatchSessionDao.observeEpisodeRewatches(LATE_EPISODE_ID).first() shouldBe 3L
    }

    @Test
    fun `should keep the viewings unchanged given the same episode is stored again`() = runTest {
        val repository = buildRepository()
        reportLateEpisode(repository, viewings = 2L)
        storeLateEpisode()

        storeLateEpisode()

        rewatchSessionDao.observeEpisodeRewatches(LATE_EPISODE_ID).first() shouldBe 2L
    }

    @Test
    fun `should keep the viewings unchanged given the sync runs again after the episode is stored`() = runTest {
        val repository = buildRepository()
        reportLateEpisode(repository, viewings = 2L)
        storeLateEpisode()

        repository.syncRewatchSessions()

        rewatchSessionDao.observeEpisodeRewatches(LATE_EPISODE_ID).first() shouldBe 2L
    }

    @Test
    fun `should restore the viewings of every stored episode given one sync reports them`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(
                listOf(
                    remoteSession(
                        episodes = listOf(
                            remoteEpisode(episodeNumber = 1L, viewings = 2L),
                            remoteEpisode(episodeNumber = 2L, viewings = 3L),
                        ),
                    ),
                ),
            ),
        )

        repository.syncRewatchSessions()

        rewatchSessionDao.observeEpisodeRewatches(EPISODE_ID).first() shouldBe 2L
        rewatchSessionDao.observeEpisodeRewatches(SECOND_EPISODE_ID).first() shouldBe 3L
    }

    @Test
    fun `should not restore removed viewings given the episode is stored again after the viewings are removed`() = runTest {
        val repository = buildRepository()
        reportLateEpisode(repository, viewings = 2L)
        storeLateEpisode()
        repository.removeEpisodeRewatches(LATE_EPISODE_ID)

        database.episodesQueries.delete(Id(LATE_EPISODE_ID))
        storeLateEpisode()

        rewatchSessionDao.observeEpisodeRewatches(LATE_EPISODE_ID).first() shouldBe 0L
    }

    @Test
    fun `should not restore removed viewings given the episode is stored again after the season viewings are removed`() = runTest {
        val repository = buildRepository()
        reportLateEpisode(repository, viewings = 2L)
        storeLateEpisode()
        repository.removeSeasonRewatches(showId = TMDB_ID, seasonNumber = 1L)

        database.episodesQueries.delete(Id(LATE_EPISODE_ID))
        storeLateEpisode()

        rewatchSessionDao.observeEpisodeRewatches(LATE_EPISODE_ID).first() shouldBe 0L
    }

    @Test
    fun `should send no restored viewing back to the provider given the episode is stored after the sync`() = runTest {
        val repository = buildRepository()
        reportLateEpisode(repository, viewings = 2L)
        storeLateEpisode()

        repository.syncPendingRewatches()

        rewatchSyncProviderDataSource.lastWrite.shouldBeNull()
    }

    @Test
    fun `should restore the viewings once given two syncs report an episode that is not stored yet`() = runTest {
        val repository = buildRepository()
        reportLateEpisode(repository, viewings = 2L)
        repository.syncRewatchSessions()

        storeLateEpisode()

        rewatchSessionDao.observeEpisodeRewatches(LATE_EPISODE_ID).first() shouldBe 2L
    }

    @Test
    fun `should not restore viewings given a later sync reports the show with no sessions`() = runTest {
        val repository = buildRepository()
        reportLateEpisode(repository, viewings = 2L)
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(ApiResponse.Success(mapOf(TMDB_ID to emptyList())))
        repository.syncRewatchSessions()

        storeLateEpisode()

        rewatchSessionDao.observeEpisodeRewatches(LATE_EPISODE_ID).first() shouldBe 0L
    }

    @Test
    fun `should not restore viewings given a later sync no longer reports the show`() = runTest {
        val repository = buildRepository()
        reportLateEpisode(repository, viewings = 2L)
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(ApiResponse.Success(emptyMap()))
        repository.syncRewatchSessions()

        storeLateEpisode()

        rewatchSessionDao.observeEpisodeRewatches(LATE_EPISODE_ID).first() shouldBe 0L
    }

    @Test
    fun `should keep the reported viewings given a later sync reads an error`() = runTest {
        val repository = buildRepository()
        reportLateEpisode(repository, viewings = 2L)
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom"),
        )
        repository.syncRewatchSessions()

        storeLateEpisode()

        rewatchSessionDao.observeEpisodeRewatches(LATE_EPISODE_ID).first() shouldBe 2L
    }

    @Test
    fun `should keep the reported viewings given a later sync is not signed in`() = runTest {
        val repository = buildRepository()
        reportLateEpisode(repository, viewings = 2L)
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(ApiResponse.Unauthenticated)
        repository.syncRewatchSessions()

        storeLateEpisode()

        rewatchSessionDao.observeEpisodeRewatches(LATE_EPISODE_ID).first() shouldBe 2L
    }

    @Test
    fun `should give each session its own viewing given one viewing is already stored in the second session`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(
                listOf(
                    remoteSession(
                        providerSessionId = OTHER_PROVIDER_SESSION_ID,
                        episodes = listOf(remoteEpisode(watchedAt = SECOND_REWATCHED_AT)),
                    ),
                ),
            ),
        )
        repository.syncRewatchSessions()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(
                listOf(
                    remoteSession(providerSessionId = PROVIDER_SESSION_ID, episodes = listOf(remoteEpisode(watchedAt = REWATCHED_AT))),
                    remoteSession(
                        providerSessionId = OTHER_PROVIDER_SESSION_ID,
                        episodes = listOf(remoteEpisode(watchedAt = SECOND_REWATCHED_AT)),
                    ),
                ),
            ),
        )

        repository.syncRewatchSessions()

        viewingsOf(EPISODE_ID) shouldBe listOf(
            OTHER_PROVIDER_SESSION_ID to SECOND_REWATCHED_AT,
            PROVIDER_SESSION_ID to REWATCHED_AT,
        )
    }

    @Test
    fun `should write only the later viewing given the provider reports one recorded on the device and a later one in another session`() =
        runTest {
            val repository = buildRepository()
            recordViewingOnDevice(repository, watchedAt = REWATCHED_AT)
            rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
                reportedSessions(
                    listOf(
                        remoteSession(providerSessionId = PROVIDER_SESSION_ID, episodes = listOf(remoteEpisode(watchedAt = REWATCHED_AT))),
                        remoteSession(
                            providerSessionId = OTHER_PROVIDER_SESSION_ID,
                            episodes = listOf(remoteEpisode(watchedAt = SECOND_REWATCHED_AT)),
                        ),
                    ),
                ),
            )

            repository.syncRewatchSessions()
            repository.syncRewatchSessions()

            viewingsOf(EPISODE_ID) shouldBe listOf(
                null to REWATCHED_AT,
                OTHER_PROVIDER_SESSION_ID to SECOND_REWATCHED_AT,
            )
        }

    @Test
    fun `should write only the later viewing given the provider reports one recorded on the device and a later one in the same session`() =
        runTest {
            val repository = buildRepository()
            recordViewingOnDevice(repository, watchedAt = REWATCHED_AT)
            rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
                reportedSessions(
                    listOf(
                        remoteSession(
                            providerSessionId = PROVIDER_SESSION_ID,
                            episodes = listOf(
                                remoteEpisode(watchedAt = REWATCHED_AT),
                                remoteEpisode(watchedAt = SECOND_REWATCHED_AT),
                            ),
                        ),
                    ),
                ),
            )

            repository.syncRewatchSessions()
            repository.syncRewatchSessions()

            viewingsOf(EPISODE_ID) shouldBe listOf(
                null to REWATCHED_AT,
                PROVIDER_SESSION_ID to SECOND_REWATCHED_AT,
            )
        }

    @Test
    fun `should write one viewing given the provider reports two at the time of one recorded on the device`() = runTest {
        val repository = buildRepository()
        recordViewingOnDevice(repository, watchedAt = REWATCHED_AT)
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(listOf(remoteSession(episodes = listOf(remoteEpisode(viewings = 2L, watchedAt = REWATCHED_AT))))),
        )

        repository.syncRewatchSessions()
        repository.syncRewatchSessions()

        viewingsOf(EPISODE_ID) shouldBe listOf(null to REWATCHED_AT, PROVIDER_SESSION_ID to REWATCHED_AT)
    }

    @Test
    fun `should leave no viewings given the viewings are removed while a sync is reading`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(listOf(remoteSession(episodes = listOf(remoteEpisode(viewings = 2L))))),
        )
        repository.syncRewatchSessions()
        val gate = CompletableDeferred<Unit>()
        rewatchSyncProviderDataSource.setReadGate(gate)
        launch { repository.syncRewatchSessions() }
        runCurrent()
        launch { repository.removeEpisodeRewatches(EPISODE_ID) }
        runCurrent()

        gate.complete(Unit)
        advanceUntilIdle()

        rewatchSessionDao.observeEpisodeRewatches(EPISODE_ID).first() shouldBe 0L
    }

    @Test
    fun `should leave no viewings given the season viewings are removed while a sync is reading`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(listOf(remoteSession(episodes = listOf(remoteEpisode(viewings = 2L))))),
        )
        repository.syncRewatchSessions()
        val gate = CompletableDeferred<Unit>()
        rewatchSyncProviderDataSource.setReadGate(gate)
        launch { repository.syncRewatchSessions() }
        runCurrent()
        launch { repository.removeSeasonRewatches(showId = TMDB_ID, seasonNumber = 1L) }
        runCurrent()

        gate.complete(Unit)
        advanceUntilIdle()

        rewatchSessionDao.observeEpisodeRewatches(EPISODE_ID).first() shouldBe 0L
    }

    @Test
    fun `should clear the restored viewings given the episode is removed from watched`() = runTest {
        val repository = buildRepository()
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(listOf(remoteSession(episodes = listOf(remoteEpisode(viewings = 2L))))),
        )
        repository.syncRewatchSessions()

        repository.removeEpisodeRewatches(EPISODE_ID)

        rewatchSessionDao.observeEpisodeRewatches(EPISODE_ID).first() shouldBe 0L
    }

    private fun viewingsOf(episodeId: Long): List<Pair<Long?, Long>> = sqlDriver.executeQuery(
        identifier = null,
        sql = """
            SELECT rewatch_session.provider_session_id, rewatch_session_episode.watched_at
            FROM rewatch_session_episode
            INNER JOIN rewatch_session ON rewatch_session.id = rewatch_session_episode.session_id
            WHERE rewatch_session_episode.episode_id = $episodeId
            ORDER BY rewatch_session_episode.id
        """.trimIndent(),
        mapper = { cursor ->
            val rows = mutableListOf<Pair<Long?, Long>>()
            while (cursor.next().value) {
                rows += cursor.getLong(0) to checkNotNull(cursor.getLong(1))
            }
            QueryResult.Value(rows.toList())
        },
        parameters = 0,
    ).value

    private suspend fun recordViewingOnDevice(repository: DefaultRewatchRepository, watchedAt: Long) {
        val sessionId = repository.startSession(showId = TMDB_ID, startedAt = STARTED_AT)!!
        repository.addEpisodeToSession(
            sessionId = sessionId,
            showId = TMDB_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            watchedAt = watchedAt,
        )
    }

    private suspend fun reportLateEpisode(repository: DefaultRewatchRepository, viewings: Long) {
        rewatchSyncProviderDataSource.setReadRewatchSessionsResponse(
            reportedSessions(listOf(remoteSession(episodes = listOf(remoteEpisode(episodeNumber = LATE_EPISODE_NUMBER, viewings = viewings))))),
        )
        repository.syncRewatchSessions()
    }

    private fun storeLateEpisode() {
        addEpisode(episodeId = LATE_EPISODE_ID, seasonId = SEASON_ID, showId = localShowId, episodeNumber = LATE_EPISODE_NUMBER)
    }

    private fun reportedSessions(sessions: List<RemoteRewatchSession>): ApiResponse<Map<Long, List<RemoteRewatchSession>>> =
        ApiResponse.Success(mapOf(TMDB_ID to sessions))

    private fun remoteSession(
        providerSessionId: Long? = PROVIDER_SESSION_ID,
        status: RemoteRewatchSessionStatus = RemoteRewatchSessionStatus.CLOSED,
        episodes: List<RemoteRewatchEpisode> = emptyList(),
    ): RemoteRewatchSession = RemoteRewatchSession(
        providerSessionId = providerSessionId,
        lastWatchedAt = REWATCHED_AT,
        status = status,
        startedAt = STARTED_AT,
        episodes = episodes,
    )

    private fun remoteEpisode(
        episodeNumber: Long = 1L,
        viewings: Long = 1L,
        watchedAt: Long? = REWATCHED_AT,
    ): RemoteRewatchEpisode = RemoteRewatchEpisode(
        seasonNumber = 1L,
        episodeNumber = episodeNumber,
        watchedAt = watchedAt,
        viewings = viewings,
    )

    private fun buildRepository(
        activeSource: () -> FakeRewatchSyncProviderDataSource? = { rewatchSyncProviderDataSource },
    ): DefaultRewatchRepository = DefaultRewatchRepository(
        rewatchSessionDao = rewatchSessionDao,
        tvShowsDao = tvShowsDao,
        activeSource = activeSource,
        syncObserver = syncObserver,
        dateTimeProvider = dateTimeProvider,
        dispatchers = dispatchers,
    )

    private fun addShow(tmdbId: Long): Long {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = "Test Show",
            overview = "Overview",
            language = "en",
            year = "2023-01-01",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        return database.tvShowQueries.getShowIdByTmdbId(Id(tmdbId)).executeAsOne().id
    }

    private fun addSeason(seasonId: Long, showId: Long) {
        database.seasonsQueries.upsert(
            id = Id(seasonId),
            show_id = Id(showId),
            season_number = 1L,
            episode_count = 10L,
            title = "Season $seasonId",
            overview = "Overview",
            image_url = null,
        )
    }

    private fun addEpisode(episodeId: Long, seasonId: Long, showId: Long, episodeNumber: Long) {
        database.episodesQueries.upsert(
            id = Id(episodeId),
            season_id = Id(seasonId),
            show_id = Id(showId),
            title = "Episode $episodeId",
            overview = "Overview",
            runtime = 40L,
            vote_count = 10L,
            ratings = 8.0,
            episode_number = episodeNumber,
            image_url = null,
            first_aired = null,
        )
    }

    private companion object {
        private const val TMDB_ID = 555L
        private const val OTHER_TMDB_ID = 556L
        private const val UNKNOWN_TMDB_ID = 909L
        private const val SEASON_ID = 100L
        private const val EPISODE_ID = 200L
        private const val SECOND_EPISODE_ID = 201L
        private const val LATE_EPISODE_ID = 299L
        private const val OTHER_SEASON_ID = 101L
        private const val OTHER_EPISODE_ID = 202L
        private const val LATE_EPISODE_NUMBER = 99L
        private const val STARTED_AT = 1_700_000_000_000L
        private const val REWATCHED_AT = 1_750_000_000_000L
        private const val SECOND_REWATCHED_AT = 1_800_000_000_000L
        private const val CLOSED_AT = 1_760_000_000_000L
        private const val PROVIDER_SESSION_ID = 7482L
        private const val OTHER_PROVIDER_SESSION_ID = 7483L
    }
}
