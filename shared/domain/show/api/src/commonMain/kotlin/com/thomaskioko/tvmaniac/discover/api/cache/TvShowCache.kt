package com.thomaskioko.tvmaniac.discover.api.cache

import com.thomaskioko.tvmaniac.datasource.cache.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.datasource.cache.Show
import kotlinx.coroutines.flow.Flow

interface TvShowCache {

    fun insert(show: Show)

    fun insert(list: List<Show>)

    fun getTvShow(showId: Int): Flow<Show>

    fun getTvShows(): Flow<List<Show>>

    fun getWatchlist(): Flow<List<Show>>

    fun getShowAirEpisodes(showId: Long): Flow<List<AirEpisodesByShowId>>

    fun updateWatchlist(showId: Int, isInWatchlist: Boolean)

    fun deleteTvShows()
}
