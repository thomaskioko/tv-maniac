package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow

public interface NextEpisodeDao {

    /**
     * Observe next episodes for all shows in the watchlist.
     * @param includeSpecials Whether to include specials (Season 0) in the calculation
     */
    public fun observeNextEpisodesForWatchlist(includeSpecials: Boolean): Flow<List<NextEpisodeWithShow>>
}
