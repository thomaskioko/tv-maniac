package com.thomaskioko.tvmaniac.discover.api.cache

import com.thomaskioko.tvmaniac.datasource.cache.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.datasource.cache.Show
import kotlinx.coroutines.flow.Flow

interface TvShowCache {

    fun insert(show: Show)

    fun insert(list: List<Show>)

    fun observeTvShow(showId: Int): Flow<Show>

    fun observeTvShows(): Flow<List<Show>>

    fun observeFollowing(): Flow<List<Show>>

    fun getShowAirEpisodes(showId: Long): Flow<List<AirEpisodesByShowId>>

    fun updateFollowingShow(showId: Int, following: Boolean)

    fun deleteTvShows()
}
