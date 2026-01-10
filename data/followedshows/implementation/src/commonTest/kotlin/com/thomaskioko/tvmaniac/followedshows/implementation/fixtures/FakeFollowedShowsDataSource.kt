package com.thomaskioko.tvmaniac.followedshows.implementation.fixtures

import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.implementation.FollowedShowsDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.IdsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.ShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse

internal class FakeFollowedShowsDataSource : FollowedShowsDataSource {
    var followedShows: List<FollowedShowEntry> = emptyList()
    var addShowsCallCount = 0
    var removeShowsCallCount = 0
    var lastAddedTraktIds: List<Long> = emptyList()
    var lastRemovedTraktIds: List<Long> = emptyList()

    override suspend fun getFollowedShows(): List<Pair<FollowedShowEntry, TraktFollowedShowResponse>> {
        return followedShows.map { entry ->
            entry to createFakeTraktResponse(entry)
        }
    }

    override suspend fun addShowsToWatchlistByTraktId(traktIds: List<Long>) {
        addShowsCallCount++
        lastAddedTraktIds = traktIds
    }

    override suspend fun removeShowsFromWatchlistByTraktId(traktIds: List<Long>) {
        removeShowsCallCount++
        lastRemovedTraktIds = traktIds
    }

    private fun createFakeTraktResponse(entry: FollowedShowEntry): TraktFollowedShowResponse {
        return TraktFollowedShowResponse(
            rank = 1,
            id = entry.traktId.toInt(),
            listedAt = "2024-01-01T00:00:00.000Z",
            type = "show",
            show = ShowResponse(
                title = "Test Show ${entry.traktId}",
                year = 2023,
                ids = IdsResponse(
                    trakt = entry.traktId,
                    slug = "test-show-${entry.traktId}",
                    imdb = "tt${entry.traktId}",
                    tmdb = entry.tmdbId ?: 0L,
                    tvdb = entry.tmdbId,
                ),
            ),
        )
    }
}
