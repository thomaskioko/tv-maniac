package com.thomaskioko.tvmaniac.seasons.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasons.api.SeasonsCache
import kotlinx.coroutines.flow.Flow

class SeasonsCacheImpl(
    private val database: TvManiacDatabase
) : SeasonsCache {

    private val seasonQueries get() = database.seasonQueries

    override fun insert(tvSeason: Season) {
        seasonQueries.insertOrReplace(
            id = tvSeason.id,
            trakt_id = tvSeason.trakt_id,
            season_number = tvSeason.season_number,
            epiosode_count = tvSeason.epiosode_count,
            name = tvSeason.name,
            overview = tvSeason.overview,
        )
    }

    override fun insert(entityList: List<Season>) {
        entityList.forEach { insert(it) }
    }

    override fun getSeasonBySeasonId(seasonId: Int): Season {
        return seasonQueries.selectBySeasonId(
            id = seasonId,
        ).executeAsOne()
    }

    override fun getSeasonsByShowId(traktId: Int): List<SelectSeasonsByShowId> {
        return seasonQueries.selectSeasonsByShowId(traktId).executeAsList()
    }

    override fun observeSeasons(traktId: Int): Flow<List<SelectSeasonsByShowId>> {
        return seasonQueries.selectSeasonsByShowId(traktId)
            .asFlow()
            .mapToList()
    }
}
