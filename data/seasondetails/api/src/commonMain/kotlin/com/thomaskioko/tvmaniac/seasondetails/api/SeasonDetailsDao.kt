package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.db.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.Season_episodes
import kotlinx.coroutines.flow.Flow

interface SeasonDetailsDao {

    fun insert(entity: Season_episodes)

    fun observeShowEpisodes(showId: Long): Flow<List<SeasonWithEpisodes>>

    fun delete(id: Long)

    fun deleteAll()
}
