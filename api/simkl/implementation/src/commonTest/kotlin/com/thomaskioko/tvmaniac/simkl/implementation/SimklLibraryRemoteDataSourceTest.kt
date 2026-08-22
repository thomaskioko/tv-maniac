package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.library.model.WatchlistShowIds
import com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAddToListResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklNotFoundBucket
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowEntry
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowIds
import com.thomaskioko.tvmaniac.simkl.testing.FakeSimklSyncRemoteDataSource
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SimklLibraryRemoteDataSourceTest {

    private val syncRemoteDataSource = FakeSimklSyncRemoteDataSource()
    private val source = SimklLibraryRemoteDataSource(syncRemoteDataSource)

    @Test
    fun `should report simkl as its provider`() {
        source.provider shouldBe SyncProviderSource.SIMKL
    }

    @Test
    fun `should send the tmdb id to the plan to watch list given a followed show`() = runTest {
        syncRemoteDataSource.setAddToListResponse(ApiResponse.Success(addToListResponse(notFound = 0)))

        source.addToWatchlist(listOf(WatchlistShowIds(tmdbId = 1396)))

        val request = syncRemoteDataSource.lastAddedToList.shouldNotBeNull()
        request.shows.map { it.ids.tmdb } shouldBe listOf("1396")
        request.shows.map { it.to } shouldBe listOf("plantowatch")
    }

    @Test
    fun `should send every followed show in one request given several are pending`() = runTest {
        syncRemoteDataSource.setAddToListResponse(ApiResponse.Success(addToListResponse(notFound = 0)))

        source.addToWatchlist(
            listOf(
                WatchlistShowIds(tmdbId = 1396),
                WatchlistShowIds(tmdbId = 87917, traktId = 1388),
            ),
        )

        val request = syncRemoteDataSource.lastAddedToList.shouldNotBeNull()
        request.shows.map { it.ids.tmdb } shouldBe listOf("1396", "87917")
    }

    @Test
    fun `should move a show to the dropped list given it is unfollowed`() = runTest {
        syncRemoteDataSource.setAddToListResponse(ApiResponse.Success(addToListResponse(notFound = 0)))

        source.removeFromWatchlist(listOf(WatchlistShowIds(tmdbId = 1396)))

        val request = syncRemoteDataSource.lastAddedToList.shouldNotBeNull()
        request.shows.map { it.ids.tmdb } shouldBe listOf("1396")
        request.shows.map { it.to } shouldBe listOf("dropped")
    }

    @Test
    fun `should report not found count given simkl could not match a show`() = runTest {
        syncRemoteDataSource.setAddToListResponse(ApiResponse.Success(addToListResponse(notFound = 2)))

        val result = source.addToWatchlist(listOf(WatchlistShowIds(tmdbId = 1396)))

        val success = result.shouldBeInstanceOf<ApiResponse.Success<WatchlistSyncResult>>()
        success.body.notFoundCount shouldBe 2
    }

    @Test
    fun `should preserve unauthenticated given the add has no session`() = runTest {
        syncRemoteDataSource.setAddToListResponse(ApiResponse.Unauthenticated)

        val result = source.addToWatchlist(listOf(WatchlistShowIds(tmdbId = 1396)))

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
    }
}

private fun addToListResponse(notFound: Int): SimklAddToListResponse = SimklAddToListResponse(
    notFound = SimklNotFoundBucket(
        shows = List(notFound) { SimklShowEntry(ids = SimklShowIds(tmdb = it.toString())) },
    ),
)
