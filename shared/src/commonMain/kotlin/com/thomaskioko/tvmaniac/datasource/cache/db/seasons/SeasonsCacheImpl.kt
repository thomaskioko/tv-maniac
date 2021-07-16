package com.thomaskioko.tvmaniac.datasource.cache.db.seasons

import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.cache.model.SeasonsEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toSeasonEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toSeasonsEntityList

class SeasonsCacheImpl(
    private val database: TvManiacDatabase
) : SeasonsCache {

    private val tvSeasonQueries get() = database.tvSeasonQueries

    override fun insert(entity: SeasonsEntity) {
        tvSeasonQueries.insertOrReplace(
            season_id = entity.seasonId.toLong(),
            tv_show_id = entity.tvShowId.toLong(),
            season_number = entity.seasonId.toLong(),
            epiosode_count = entity.episodeCount.toLong(),
            name = entity.name,
            overview = entity.overview,
        )
    }

    override fun insert(entityList: List<SeasonsEntity>) {
        entityList.forEach { insert(it) }
    }


    override fun getSeasonBySeasonId(seasonId: Int): SeasonsEntity {
        return tvSeasonQueries.selectBySeasonId(
            season_id = seasonId.toLong()
        ).executeAsOne()
            .toSeasonEntity()
    }

    override fun getSeasonListByTvShowId(tvShowId: Int): List<SeasonsEntity> {
        return tvSeasonQueries.selectSeasonsByShowId(
            tv_show_id = tvShowId.toLong()
        ).executeAsList()
            .toSeasonsEntityList()
    }

}