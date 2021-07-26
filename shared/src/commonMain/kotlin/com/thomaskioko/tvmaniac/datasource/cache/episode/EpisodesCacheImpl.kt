package com.thomaskioko.tvmaniac.datasource.cache.episode

import com.thomaskioko.tvmaniac.datasource.cache.Episode
import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase

class EpisodesCacheImpl(
    private val database: TvManiacDatabase
) : EpisodesCache {

    private val episodeQueries get() = database.episodeQueries

    override fun insert(entity: com.thomaskioko.tvmaniac.presentation.model.Episode) {
        episodeQueries.insertOrReplace(
            id = entity.id.toLong(),
            season_id = entity.seasonId.toLong(),
            name = entity.name,
            overview = entity.overview,
            episode_season_number = entity.seasonNumber.toLong(),
            image_url = entity.imageUrl,
            vote_average = entity.voteAverage,
            vote_count = entity.voteCount.toLong(),
            episode_number = entity.episodeNumber
        )
    }

    override fun insert(list: List<com.thomaskioko.tvmaniac.presentation.model.Episode>) {
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