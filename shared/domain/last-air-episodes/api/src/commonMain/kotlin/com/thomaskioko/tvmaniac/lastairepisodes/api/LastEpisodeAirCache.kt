package com.thomaskioko.tvmaniac.lastairepisodes.api

import com.thomaskioko.tvmaniac.core.db.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.core.db.Last_episode
import kotlinx.coroutines.flow.Flow

interface LastEpisodeAirCache {

    fun insert(episode: Last_episode)

    fun insert(list: List<Last_episode>)

    fun getShowAirEpisodes(showId: Int): Flow<List<AirEpisodesByShowId>>
}
