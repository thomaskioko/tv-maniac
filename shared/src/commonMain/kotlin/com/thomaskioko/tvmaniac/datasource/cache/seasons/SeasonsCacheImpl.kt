package com.thomaskioko.tvmaniac.datasource.cache.seasons

import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.cache.Tv_season

class SeasonsCacheImpl(
    private val database: TvManiacDatabase
) : SeasonsCache {

    private val seasonQueries get() = database.tvSeasonQueries

    override fun insert(tvSeason: Tv_season) {
        seasonQueries.insertOrReplace(
            id = tvSeason.id,
            tv_show_id = tvSeason.tv_show_id,
            season_number = tvSeason.season_number,
            epiosode_count = tvSeason.epiosode_count,
            name = tvSeason.name,
            overview = tvSeason.overview,
        )
    }

    override fun insert(entityList: List<Tv_season>) {
        entityList.forEach { insert(it) }
    }


    override fun getSeasonBySeasonId(seasonId: Int): Tv_season {
        return seasonQueries.selectBySeasonId(
            id = seasonId.toLong(),
        ).executeAsOne()
    }

    override fun getSeasonsByTvShowId(tvShowId: Int): List<SelectSeasonsByShowId> {
        return seasonQueries.selectSeasonsByShowId(
            tv_show_id = tvShowId.toLong()
        ).executeAsList()
    }

    override fun updateSeasonEpisodesIds(seasonId: Int, episodeIds: List<Int>) {
        seasonQueries.updateEpisodes(
            id = seasonId.toLong(),
            episode_ids = episodeIds
        )
    }

}