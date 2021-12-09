package com.thomaskioko.tvmaniac.datasource.cache.shows

import com.thomaskioko.tvmaniac.datasource.cache.Show
import kotlinx.coroutines.flow.Flow

interface TvShowCache {

    fun insert(show: Show)

    fun insert(list: List<Show>)

    fun getTvShow(showId: Int): Flow<Show>

    fun getTvShows(): Flow<List<Show>>

    fun getWatchlist(): Flow<List<Show>>

    fun updateShowDetails(showId: Int, showStatus: String, seasonIds: List<Int>)

    fun updateWatchlist(showId: Int, isInWatchlist: Boolean)

    fun deleteTvShows()
}
