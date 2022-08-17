package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.core.db.Season_with_episodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import kotlinx.coroutines.flow.Flow

interface SeasonWithEpisodesCache {

    fun insert(entity: Season_with_episodes)

    fun insert(list: List<Season_with_episodes>)

    fun observeShowEpisodes(showId: Long): Flow<List<SelectSeasonWithEpisodes>>
}
