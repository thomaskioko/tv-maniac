package com.thomaskioko.tvmaniac.upnext.api

import com.thomaskioko.tvmaniac.upnext.api.model.CompletedShow
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow

public interface UpNextRepository {

    public fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>>

    public fun observeCompletedShows(): Flow<List<CompletedShow>>

    public suspend fun saveUpNextSortOption(sortOption: String)

    public fun observeUpNextSortOption(): Flow<String>
}
