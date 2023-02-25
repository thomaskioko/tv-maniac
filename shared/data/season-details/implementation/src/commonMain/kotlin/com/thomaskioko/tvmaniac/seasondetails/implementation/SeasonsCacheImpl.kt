package com.thomaskioko.tvmaniac.seasondetails.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.Season_episodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsCache
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

class SeasonsCacheImpl(
    private val database: TvManiacDatabase,
    private val coroutineContext: CoroutineContext
) : SeasonsCache {

    private val seasonQueries get() = database.seasonQueries

    override fun insertSeason(season: Season) {
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

    override fun insertSeasons(entityList: List<Season>) {
        entityList.forEach { insertSeason(it) }
    }

    override fun observeSeasons(traktId: Long): Flow<List<Season>> {
        return seasonQueries.selectBySeasonId(traktId)
            .asFlow()
            .mapToList(coroutineContext)
    }

    override fun insert(entity: Season_episodes) {
        database.transaction {
            database.seasonEpisodesQueries.insertOrReplace(
                show_id = entity.show_id,
                season_id = entity.season_id,
                season_number = entity.season_number
            )
        }
    }

    override fun observeShowEpisodes(showId: Long): Flow<List<SelectSeasonWithEpisodes>> =
        database.seasonEpisodesQueries.selectSeasonWithEpisodes(showId)
            .asFlow()
            .mapToList(coroutineContext)
}
