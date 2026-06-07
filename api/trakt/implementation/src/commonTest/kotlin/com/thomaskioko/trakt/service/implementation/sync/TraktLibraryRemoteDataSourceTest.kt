package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.library.model.RemoteFollowedShow
import com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult
import com.thomaskioko.tvmaniac.trakt.api.model.IdsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.ShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRemoveShowFromListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddShowToListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddedShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktDeletedShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktExistingShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktNotFoundShows
import com.thomaskioko.tvmaniac.trakt.api.model.TraktNotFoundShowsResponse
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktListRemoteDataSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Instant

class TraktLibraryRemoteDataSourceTest {

    private val remoteDataSource = FakeTraktListRemoteDataSource()
    private val source = TraktLibraryRemoteDataSource(remoteDataSource)

    @Test
    fun `should report trakt as its provider`() {
        source.provider shouldBe ConnectedProvider.TRAKT
    }

    @Test
    fun `should map followed shows to watchlist shows given successful response`() = runTest {
        remoteDataSource.setWatchList(
            ApiResponse.Success(
                listOf(
                    followedShow(traktId = 1, tmdbId = 10, title = "Severance", year = 2022, listedAt = "2025-01-01T00:00:00Z"),
                ),
            ),
        )

        val result = source.getWatchlist()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemoteFollowedShow>>>()
        success.body shouldBe listOf(
            RemoteFollowedShow(
                showId = 1,
                tmdbId = 10,
                title = "Severance",
                year = 2022,
                followedAt = Instant.parse("2025-01-01T00:00:00Z"),
            ),
        )
    }

    @Test
    fun `should report not found count given add response`() = runTest {
        remoteDataSource.setAddShowsToWatchList(ApiResponse.Success(addResponse(notFound = 2)))

        val result = source.addToWatchlist(listOf(1, 2, 3))

        val success = result.shouldBeInstanceOf<ApiResponse.Success<WatchlistSyncResult>>()
        success.body.notFoundCount shouldBe 2
    }

    @Test
    fun `should report not found count given remove response`() = runTest {
        remoteDataSource.setRemoveShowsFromWatchList(ApiResponse.Success(removeResponse(notFound = 1)))

        val result = source.removeFromWatchlist(listOf(9))

        val success = result.shouldBeInstanceOf<ApiResponse.Success<WatchlistSyncResult>>()
        success.body.notFoundCount shouldBe 1
    }

    @Test
    fun `should preserve unauthenticated given watchlist pull has no session`() = runTest {
        remoteDataSource.setWatchList(ApiResponse.Unauthenticated)

        val result = source.getWatchlist()

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
    }
}

private fun followedShow(
    traktId: Long,
    tmdbId: Long,
    title: String,
    year: Int?,
    listedAt: String,
): TraktFollowedShowResponse = TraktFollowedShowResponse(
    rank = 1,
    id = traktId.toInt(),
    listedAt = listedAt,
    type = "show",
    show = ShowResponse(
        title = title,
        year = year,
        ids = IdsResponse(slug = "slug-$traktId", trakt = traktId, tmdb = tmdbId),
    ),
)

private fun addResponse(notFound: Int): TraktAddShowToListResponse = TraktAddShowToListResponse(
    added = TraktAddedShowsResponse(shows = 0),
    existing = TraktExistingShowsResponse(shows = 0),
    notFound = TraktNotFoundShowsResponse(shows = List(notFound) { TraktNotFoundShows(trakt = it, tmdb = it) }),
    list = TraktListResponse(itemCount = 0, updateAdd = ""),
)

private fun removeResponse(notFound: Int): TraktAddRemoveShowFromListResponse = TraktAddRemoveShowFromListResponse(
    deleted = TraktDeletedShowsResponse(shows = 0),
    notFound = TraktNotFoundShowsResponse(shows = List(notFound) { TraktNotFoundShows(trakt = it, tmdb = it) }),
    list = TraktListResponse(itemCount = 0, updateAdd = ""),
)
