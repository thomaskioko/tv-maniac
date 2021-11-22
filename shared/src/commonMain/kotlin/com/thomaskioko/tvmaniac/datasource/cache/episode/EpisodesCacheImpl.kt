package com.thomaskioko.tvmaniac.datasource.cache.episode

import com.thomaskioko.tvmaniac.datasource.cache.Episode
import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.cache.Episode as EpisodeCache

class EpisodesCacheImpl(
    private val database: TvManiacDatabase
) : EpisodesCache {

    private val episodeQueries get() = database.episodeQueries

    override fun insert(entity: EpisodeCache) {
        episodeQueries.insertOrReplace(
            id = entity.id,
            season_id = entity.season_id,
            name = entity.name,
            overview = entity.overview,
            episode_season_number = entity.episode_season_number,
            image_url = entity.image_url,
            vote_average = entity.vote_average,
            vote_count = entity.vote_count,
            episode_number = entity.episode_number
        )
    }

    override fun insert(list: List<EpisodeCache>) {
        list.map { insert(it) }
    }

    override fun getEpisodeByEpisodeId(episodeId: Int): Episode {
        return episodeQueries.episodeById(
            id = episodeId.toLong()
        ).executeAsOne()
    }

    override fun getEpisodesBySeasonId(seasonId: Int): List<EpisodesBySeasonId> {
        return episodeQueries.episodesBySeasonId(
            season_id = seasonId.toLong()
        ).executeAsList()
    }
}
