package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId

fun List<EpisodesBySeasonId>.toEpisodeEntityList(): List<EpisodeUiModel> {
    return map { it.toEpisodeEntity() }
}

fun EpisodesBySeasonId.toEpisodeEntity(): EpisodeUiModel {
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
