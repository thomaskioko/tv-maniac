package com.thomaskioko.tvmaniac.seasons.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasons.api.SeasonsCache
import kotlinx.coroutines.flow.Flow

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

    override fun getSeasonBySeasonId(seasonId: Long): Season {
        return seasonQueries.selectBySeasonId(
            id = seasonId,
        ).executeAsOne()
    }

    override fun observeSeasons(tvShowId: Long): Flow<List<SelectSeasonsByShowId>> {
        return seasonQueries.selectSeasonsByShowId(tvShowId)
            .asFlow()
            .mapToList()
    }

    override fun updateSeasonEpisodesIds(seasonId: Long, episodeIds: List<Int>) {
        seasonQueries.updateEpisodes(
            id = seasonId,
            episode_ids = episodeIds
        )
    }
}
