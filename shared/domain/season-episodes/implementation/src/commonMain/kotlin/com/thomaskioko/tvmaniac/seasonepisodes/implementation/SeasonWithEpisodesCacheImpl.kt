package com.thomaskioko.tvmaniac.seasonepisodes.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.datasource.cache.Season_with_episodes
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesCache
import kotlinx.coroutines.flow.Flow

class SeasonWithEpisodesCacheImpl(
    private val database: TvManiacDatabase
) : SeasonWithEpisodesCache {

    private val query get() = database.showWithEpisodesQueries

    override fun insert(entity: Season_with_episodes) {
        database.transaction {
            query.insertOrReplace(
                show_id = entity.show_id,
                season_id = entity.season_id,
                season_number = entity.season_number
            )
        }
    }

    override fun insert(list: List<Season_with_episodes>) {
        list.map { insert(it) }
    }

    override fun observeShowEpisodes(showId: Long): Flow<List<SelectSeasonWithEpisodes>> =
        query.selectSeasonWithEpisodes(showId)
            .asFlow()
            .mapToList()
}
