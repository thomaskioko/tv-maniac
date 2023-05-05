package com.thomaskioko.tvmaniac.seasondetails.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Season_episodes
import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsCache
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class SeasonsCacheImpl(
    private val database: TvManiacDatabase,
    private val dispatcher: AppCoroutineDispatchers,
) : SeasonsCache {

    private val seasonQueries get() = database.seasonQueries

    override fun insertSeason(season: Seasons) {
        database.transaction {
            seasonQueries.insertOrReplace(
                id = season.id,
                show_trakt_id = season.show_trakt_id,
                season_number = season.season_number,
                episode_count = season.episode_count,
                name = season.name,
                overview = season.overview,
            )
        }
    }

    override fun insertSeasons(entityList: List<Seasons>) {
        entityList.forEach { insertSeason(it) }
    }

    override fun observeSeasons(traktId: Long): Flow<List<Seasons>> {
        return seasonQueries.selectBySeasonId(traktId)
            .asFlow()
            .mapToList(dispatcher.io)
    }

    override fun insert(entity: Season_episodes) {
        database.transaction {
            database.seasonEpisodesQueries.insertOrReplace(
                show_id = entity.show_id,
                season_id = entity.season_id,
                season_number = entity.season_number,
            )
        }
    }

    override fun observeShowEpisodes(showId: Long): Flow<List<SelectSeasonWithEpisodes>> =
        database.seasonEpisodesQueries.selectSeasonWithEpisodes(showId)
            .asFlow()
            .mapToList(dispatcher.io)
}
