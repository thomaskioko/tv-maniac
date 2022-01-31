package com.thomaskioko.tvmaniac.episodes.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import kotlinx.coroutines.flow.Flow
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
            image_url = entity.image_url,
            vote_average = entity.vote_average,
            vote_count = entity.vote_count,
            episode_number = entity.episode_number
        )
    }

    override fun insert(list: List<EpisodeCache>) {
        list.map { insert(it) }
    }

    override fun observeEpisode(seasonId: Long): Flow<List<EpisodesBySeasonId>> =
        episodeQueries.episodesBySeasonId(seasonId)
            .asFlow()
            .mapToList()
}
