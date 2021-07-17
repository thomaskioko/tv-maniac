package com.thomaskioko.tvmaniac.datasource.cache.episode

import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.cache.model.EpisodeEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toEpisodeEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toEpisodeEntityList

class EpisodesCacheImpl(
    private val database: TvManiacDatabase
) : EpisodesCache {

    private val episodeQueries get() = database.episodeQueries

    override fun insert(entity: EpisodeEntity) {
        episodeQueries.insertOrReplace(
            id = entity.id.toLong(),
            season_id = entity.seasonId.toLong(),
            name = entity.name,
            overview = entity.overview,
            episode_season_number = entity.seasonNumber.toLong(),
            image_url = entity.imageUrl,
            vote_average = entity.voteAverage,
            vote_count = entity.voteCount.toLong(),
            episode_number = entity.episodeNumber.toLong()
        )
    }

    override fun insert(entityList: List<EpisodeEntity>) {
        entityList.map { insert(it) }
    }

    override fun getEpisodeByEpisodeId(episodeId: Int): EpisodeEntity {
        return episodeQueries.episodeById(
            id = episodeId.toLong()
        ).executeAsOne()
            .toEpisodeEntity()
    }

    override fun getEpisodesBySeasonId(seasonId: Int): List<EpisodeEntity> {
        return episodeQueries.episodesBySeasonId(
            season_id = seasonId.toLong()
        ).executeAsList()
            .toEpisodeEntityList()
    }


}