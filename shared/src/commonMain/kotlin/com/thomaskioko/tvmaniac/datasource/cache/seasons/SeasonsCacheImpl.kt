package com.thomaskioko.tvmaniac.datasource.cache.seasons

import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase

class SeasonsCacheImpl(
    private val database: TvManiacDatabase
) : SeasonsCache {

    private val seasonQueries get() = database.seasonQueries

    override fun insert(tvSeason: Season) {
        seasonQueries.insertOrReplace(
            id = tvSeason.id,
            tv_show_id = tvSeason.tv_show_id,
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
