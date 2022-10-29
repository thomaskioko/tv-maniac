package com.thomaskioko.tvmaniac.seasonepisodes.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Season_with_episodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
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

    override fun observeShowEpisodes(showId: Int): Flow<List<SelectSeasonWithEpisodes>> =
        query.selectSeasonWithEpisodes(showId)
            .asFlow()
            .mapToList()
}
