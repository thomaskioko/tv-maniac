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
    var lastAddedTmdbIds: List<Long> = emptyList()
    var lastRemovedTmdbIds: List<Long> = emptyList()

    override suspend fun getFollowedShows(): List<Pair<FollowedShowEntry, TraktFollowedShowResponse>> {
        return followedShows.map { entry ->
            entry to createFakeTraktResponse(entry)
        }
    }

    override suspend fun addShowsToWatchlist(tmdbIds: List<Long>) {
        addShowsCallCount++
        lastAddedTmdbIds = tmdbIds
    }

    override suspend fun removeShowsFromWatchlist(tmdbIds: List<Long>) {
        removeShowsCallCount++
        lastRemovedTmdbIds = tmdbIds
    }

    private fun createFakeTraktResponse(entry: FollowedShowEntry): TraktFollowedShowResponse {
        return TraktFollowedShowResponse(
            rank = 1,
            id = entry.traktId?.toInt() ?: 0,
            listedAt = "2024-01-01T00:00:00.000Z",
            type = "show",
            show = ShowResponse(
                title = "Test Show ${entry.tmdbId}",
                year = 2023,
                ids = IdsResponse(
                    trakt = entry.traktId?.toInt() ?: 0,
                    slug = "test-show-${entry.tmdbId}",
                    imdb = "tt${entry.tmdbId}",
                    tmdb = entry.tmdbId.toInt(),
                    tvdb = entry.tmdbId.toInt(),
                ),
            ),
        )
    }
}
