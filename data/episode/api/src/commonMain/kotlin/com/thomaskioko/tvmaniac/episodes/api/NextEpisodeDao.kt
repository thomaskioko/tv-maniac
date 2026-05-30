package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.upnext.api.model.CompletedShow
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow

public interface NextEpisodeDao {

    /**
     * Observe next episodes for all shows in the watchlist.
     * @param includeSpecials Whether to include specials (Season 0) in the calculation
     */
    public fun observeNextEpisodesForWatchlist(includeSpecials: Boolean): Flow<List<NextEpisodeWithShow>>

    /** Observe watchlist shows the user has finished, where every aired episode is watched. */
    public fun observeCompletedShows(): Flow<List<CompletedShow>>
}
