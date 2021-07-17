package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.Episode
import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.datasource.cache.model.EpisodeEntity
import com.thomaskioko.tvmaniac.datasource.network.model.SeasonResponse


fun SeasonResponse.toEpisodeEntityList(): List<EpisodeEntity> {
    return episodes.map { episodeResponse ->
        EpisodeEntity(
            id = episodeResponse.id,
            seasonId = id,
            name = episodeResponse.name,
            overview = episodeResponse.overview,
            seasonNumber = episodeResponse.season_number,
            imageUrl = episodeResponse.still_path,
            voteAverage = episodeResponse.vote_average,
            voteCount = episodeResponse.vote_count,
            episodeNumber = episodeResponse.episode_number
        )
    }
}

fun List<EpisodesBySeasonId>.toEpisodeEntityList(): List<EpisodeEntity> {
    return map { it.toEpisodeEntity() }
}

fun EpisodesBySeasonId.toEpisodeEntity(): EpisodeEntity {
    return EpisodeEntity(
        id = id.toInt(),
        seasonId = season_id.toInt(),
        name = name,
        overview = overview,
        seasonNumber = episode_season_number.toInt(),
        imageUrl = image_url,
        voteAverage = vote_average,
        voteCount = vote_count.toInt(),
        episodeNumber = episode_number.toInt()
    )
}


fun Episode.toEpisodeEntity(): EpisodeEntity {
    return EpisodeEntity(
        id = id.toInt(),
        seasonId = season_id.toInt(),
        name = name,
        overview = overview,
        seasonNumber = episode_season_number.toInt(),
        imageUrl = image_url,
        voteAverage = vote_average,
        voteCount = vote_count.toInt(),
        episodeNumber = episode_number.toInt()
    )
}