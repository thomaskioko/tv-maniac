package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.remote.api.model.SeasonResponse
import com.thomaskioko.tvmaniac.episodes.api.EpisodeUiModel
import com.thomaskioko.tvmaniac.shared.core.util.StringUtil.formatPosterPath
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

fun SeasonResponse.toEpisodeEntityList(): List<EpisodeUiModel> {
    return episodes.map { episodeResponse ->
        EpisodeUiModel(
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

fun EpisodeCache.toEpisodeEntity(): EpisodeUiModel {
    return EpisodeUiModel(
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
