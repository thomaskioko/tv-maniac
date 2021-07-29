package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.datasource.network.model.SeasonResponse
import com.thomaskioko.tvmaniac.presentation.model.Episode
import com.thomaskioko.tvmaniac.util.StringUtil.formatPosterPath
import com.thomaskioko.tvmaniac.datasource.cache.Episode as EpisodeCache


fun SeasonResponse.toEpisodeCacheList(): List<EpisodeCache> {
    return episodes.map { episodeResponse ->
        EpisodeCache(
            id = episodeResponse.id.toLong(),
            season_id = id.toLong(),
            name = episodeResponse.name,
            overview = episodeResponse.overview,
            episode_season_number = episodeResponse.season_number.toLong(),
            image_url = formatPosterPath(episodeResponse.still_path),
            vote_average = episodeResponse.vote_average,
            vote_count = episodeResponse.vote_count.toLong(),
            episode_number = episodeResponse.episode_number.toString().padStart(2, '0')
        )
    }
}

fun SeasonResponse.toEpisodeEntityList(): List<Episode> {
    return episodes.map { episodeResponse ->
        Episode(
            id = episodeResponse.id,
            seasonId = id,
            name = episodeResponse.name,
            overview = episodeResponse.overview,
            seasonNumber = episodeResponse.season_number,
            imageUrl = formatPosterPath(episodeResponse.still_path),
            voteAverage = episodeResponse.vote_average,
            voteCount = episodeResponse.vote_count,
            episodeNumber = episodeResponse.episode_number.toString().padStart(2, '0')
        )
    }
}

fun List<EpisodesBySeasonId>.toEpisodeEntityList(): List<Episode> {
    return map { it.toEpisodeEntity() }
}

fun EpisodesBySeasonId.toEpisodeEntity(): Episode {
    return Episode(
        id = id.toInt(),
        seasonId = season_id.toInt(),
        name = name,
        overview = overview,
        seasonNumber = episode_season_number.toInt(),
        imageUrl = image_url,
        voteAverage = vote_average,
        voteCount = vote_count.toInt(),
        episodeNumber = episode_number
    )
}


fun EpisodeCache.toEpisodeEntity(): Episode {
    return Episode(
        id = id.toInt(),
        seasonId = season_id.toInt(),
        name = name,
        overview = overview,
        seasonNumber = episode_season_number.toInt(),
        imageUrl = image_url,
        voteAverage = vote_average,
        voteCount = vote_count.toInt(),
        episodeNumber = episode_number
    )
}