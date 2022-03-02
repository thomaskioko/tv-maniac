package com.thomaskioko.tvmaniac.showcommon.api.cache

import com.thomaskioko.tvmaniac.datasource.cache.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.datasource.cache.Show
import kotlinx.coroutines.flow.Flow

interface TvShowCache {

    fun insert(show: Show)

    fun insert(list: List<Show>)

    fun observeTvShow(showId: Long): Flow<Show>

    fun observeTvShows(): Flow<List<Show>>

    fun observeFollowing(): Flow<List<Show>>

    fun getShowAirEpisodes(showId: Long): Flow<List<AirEpisodesByShowId>>

    fun updateFollowingShow(showId: Long, following: Boolean)

    fun deleteTvShows()
}
