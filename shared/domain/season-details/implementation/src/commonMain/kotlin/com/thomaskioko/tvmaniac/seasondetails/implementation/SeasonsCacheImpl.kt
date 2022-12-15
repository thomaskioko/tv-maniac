package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.Season_with_episodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsCache
import kotlinx.coroutines.flow.Flow

class SeasonsCacheImpl(
    private val database: TvManiacDatabase
) : SeasonsCache {

    private val seasonQueries get() = database.seasonQueries
    private val query get() = database.showWithEpisodesQueries

    override fun insertSeason(tvSeason: Season) {
        database.transaction {
            seasonQueries.insertOrReplace(
                id = tvSeason.id,
                show_id = tvSeason.show_id,
                season_number = tvSeason.season_number,
                epiosode_count = tvSeason.epiosode_count,
                name = tvSeason.name,
                overview = tvSeason.overview,
            )
        }
    }

    override fun insertSeasons(entityList: List<Season>) {
        entityList.forEach { insertSeason(it) }
    }

    override fun observeSeasons(traktId: Int): Flow<List<SelectSeasonsByShowId>> {
        return seasonQueries.selectSeasonsByShowId(traktId)
            .asFlow()
            .mapToList()
    }

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
