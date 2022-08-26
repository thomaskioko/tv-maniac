package com.thomaskioko.tvmaniac.showcommon.api.cache

import com.thomaskioko.tvmaniac.core.db.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.core.db.Show
import kotlinx.coroutines.flow.Flow

interface TvShowCache {

    fun insert(show: Show)

    fun insert(list: List<Show>)

    fun updateShow(show: Show)

    fun observeTvShow(showId: Long): Flow<Show>

    fun observeTvShows(): Flow<List<Show>>

    fun observeFollowing(): Flow<List<Show>>

    fun getShowAirEpisodes(showId: Long): Flow<List<AirEpisodesByShowId>>

    fun updateFollowingShow(showId: Long, following: Boolean)

    fun deleteTvShows()
}
