package com.thomaskioko.tvmaniac.datasource.cache.shows

import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory
import kotlinx.coroutines.flow.Flow

interface TvShowCache {

    fun insert(show: Show)

    fun insert(list: List<Show>)

    fun getTvShow(showId: Int): Flow<Show>

    fun getTvShows(): List<Show>

    fun getWatchlist(): Flow<List<Show>>

    fun getTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<Show>

    fun getFeaturedTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<Show>

    fun updateShowDetails(showId: Int, showStatus: String, seasonIds: List<Int>)

    fun updateWatchlist(showId: Int, isInWatchlist: Boolean)

    fun deleteTvShows()
}