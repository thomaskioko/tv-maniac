package com.thomaskioko.tvmaniac.upnext.api

import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow

public interface UpNextRepository {

    public fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>>

    public fun observeFollowedShowsCount(): Flow<Int>

    public suspend fun fetchUpNextEpisodes(forceRefresh: Boolean)

    public suspend fun saveUpNextSortOption(sortOption: String)

    public fun observeUpNextSortOption(): Flow<String>

    public suspend fun updateUpNextForShow(showTraktId: Long, forceRefresh: Boolean = false)

    public suspend fun fetchUpNext(
        showTraktId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    )
}
