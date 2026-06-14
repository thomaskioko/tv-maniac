package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAllItemsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowEntry
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowIds
import com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedShow
import com.thomaskioko.tvmaniac.simkl.testing.FakeSimklSyncRemoteDataSource
import com.thomaskioko.tvmaniac.startwatching.api.RemotePlanToWatchShow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Instant

internal class SimklStartWatchingRemoteDataSourceTest {

    private val syncDataSource = FakeSimklSyncRemoteDataSource()
    private val source = SimklStartWatchingRemoteDataSource(syncDataSource)

    @Test
    fun `should report simkl as its provider`() {
        source.provider shouldBe AccountProvider.SIMKL
    }

    @Test
    fun `should return only plantowatch shows given mixed-status response`() = runTest {
        syncDataSource.setAllWatchedShows(
            ApiResponse.Success(
                SimklAllItemsResponse(
                    shows = listOf(
                        simklWatchedShow(simklId = 1, tmdb = "100", status = "plantowatch", title = "Plan Show"),
                        simklWatchedShow(simklId = 2, tmdb = "200", status = "watching", title = "Watching Show"),
                        simklWatchedShow(simklId = 3, tmdb = "300", status = "completed", title = "Completed Show"),
                        simklWatchedShow(simklId = 4, tmdb = "400", status = "hold", title = "Hold Show"),
                    ),
                ),
            ),
        )

        val result = source.getPlanToWatch()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemotePlanToWatchShow>>>()
        success.body.map { it.title } shouldBe listOf("Plan Show")
    }

    @Test
    fun `should map plan-to-watch show fields correctly given valid response`() = runTest {
        syncDataSource.setAllWatchedShows(
            ApiResponse.Success(
                SimklAllItemsResponse(
                    shows = listOf(
                        simklWatchedShow(
                            simklId = 583436,
                            tmdb = "62417",
                            status = "plantowatch",
                            title = "Emerald City",
                            year = 2017,
                            imdb = "tt3579018",
                            lastWatchedAt = "2025-01-15T10:00:00Z",
                        ),
                    ),
                ),
            ),
        )

        val result = source.getPlanToWatch()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemotePlanToWatchShow>>>()
        val show = success.body.single()
        show.tmdbId shouldBe 62417L
        show.imdbId shouldBe "tt3579018"
        show.providerShowId shouldBe "583436"
        show.provider shouldBe AccountProvider.SIMKL
        show.title shouldBe "Emerald City"
        show.year shouldBe 2017
        show.followedAt shouldBe Instant.parse("2025-01-15T10:00:00Z")
    }

    @Test
    fun `should return empty list given no shows have plantowatch status`() = runTest {
        syncDataSource.setAllWatchedShows(
            ApiResponse.Success(
                SimklAllItemsResponse(
                    shows = listOf(
                        simklWatchedShow(simklId = 1, tmdb = "100", status = "watching", title = "Active Show"),
                        simklWatchedShow(simklId = 2, tmdb = "200", status = "completed", title = "Done Show"),
                    ),
                ),
            ),
        )

        val result = source.getPlanToWatch()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemotePlanToWatchShow>>>()
        success.body.shouldBeEmpty()
    }

    @Test
    fun `should use epoch zero as followedAt given show has no last_watched_at`() = runTest {
        syncDataSource.setAllWatchedShows(
            ApiResponse.Success(
                SimklAllItemsResponse(
                    shows = listOf(
                        simklWatchedShow(
                            simklId = 1,
                            tmdb = "100",
                            status = "plantowatch",
                            title = "No Date Show",
                            lastWatchedAt = null,
                        ),
                    ),
                ),
            ),
        )

        val result = source.getPlanToWatch()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemotePlanToWatchShow>>>()
        success.body.single().followedAt shouldBe Instant.fromEpochSeconds(0)
    }

    @Test
    fun `should preserve unauthenticated given sync source returns unauthenticated`() = runTest {
        syncDataSource.setAllWatchedShows(ApiResponse.Unauthenticated)

        val result = source.getPlanToWatch()

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
    }

    @Test
    fun `should include show with null tmdb id in result given simkl show has no tmdb`() = runTest {
        syncDataSource.setAllWatchedShows(
            ApiResponse.Success(
                SimklAllItemsResponse(
                    shows = listOf(
                        simklWatchedShow(
                            simklId = 999,
                            tmdb = null,
                            status = "plantowatch",
                            title = "No TMDB Show",
                        ),
                    ),
                ),
            ),
        )

        val result = source.getPlanToWatch()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemotePlanToWatchShow>>>()
        success.body.single().tmdbId shouldBe null
    }
}

private fun simklWatchedShow(
    simklId: Long,
    tmdb: String?,
    status: String,
    title: String,
    year: Int? = 2020,
    imdb: String? = null,
    lastWatchedAt: String? = null,
): SimklWatchedShow = SimklWatchedShow(
    status = status,
    lastWatchedAt = lastWatchedAt,
    show = SimklShowEntry(
        title = title,
        year = year,
        ids = SimklShowIds(
            simkl = simklId,
            tmdb = tmdb,
            imdb = imdb,
        ),
    ),
)
