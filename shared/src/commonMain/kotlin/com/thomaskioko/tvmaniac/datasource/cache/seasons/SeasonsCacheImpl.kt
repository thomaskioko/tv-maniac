package com.thomaskioko.tvmaniac.datasource.cache.seasons

import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase

class SeasonsCacheImpl(
    private val database: TvManiacDatabase
) : SeasonsCache {

    private val seasonQueries get() = database.seasonQueries

    override fun insert(entity: com.thomaskioko.tvmaniac.presentation.model.Season) {
        seasonQueries.insertOrReplace(
            id = entity.seasonId.toLong(),
            tv_show_id = entity.tvShowId.toLong(),
            season_number = entity.seasonNumber.toLong(),
            epiosode_count = entity.episodeCount.toLong(),
            name = entity.name,
            overview = entity.overview,
        )
    }

    override fun insert(entityList: List<com.thomaskioko.tvmaniac.presentation.model.Season>) {
        entityList.forEach { insert(it) }
    }


    override fun getSeasonBySeasonId(seasonId: Int): Season {
        return seasonQueries.selectBySeasonId(
            id = seasonId.toLong(),
        ).executeAsOne()
    }

    override fun getSeasonsByTvShowId(tvShowId: Int): List<SelectSeasonsByShowId> {
        return seasonQueries.selectSeasonsByShowId(
            tv_show_id = tvShowId.toLong()
        ).executeAsList()
    }

    override fun updateSeasonEpisodes(entity: com.thomaskioko.tvmaniac.presentation.model.Season) {
        seasonQueries.updateEpisodes(
            id = entity.seasonId.toLong(),
            episodes = entity.episodeList
        )
    }

}