package com.thomaskioko.tvmaniac.seasons.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class SeasonsDaoImpl(
    private val database: TvManiacDatabase,
    private val dispatcher: AppCoroutineDispatchers,
) : SeasonsDao {

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
        return seasonQueries.seasonById(traktId)
            .asFlow()
            .mapToList(dispatcher.io)
    }

    override fun delete(id: Long) {
        seasonQueries.delete(id)
    }

    override fun deleteAll() {
        database.transaction {
            seasonQueries.deleteAll()
        }
    }
}
