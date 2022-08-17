package com.thomaskioko.tvmaniac.episodes.api.mapper

import com.thomaskioko.tvmaniac.core.db.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeUiModel

fun List<EpisodesBySeasonId>.toEpisodeEntityList(): List<EpisodeUiModel> {
    return map { it.toEpisodeEntity() }
}

fun EpisodesBySeasonId.toEpisodeEntity(): EpisodeUiModel {
    return EpisodeUiModel(
        id = id,
        seasonId = season_id,
        name = name,
        overview = overview,
        imageUrl = image_url,
        voteAverage = vote_average,
        voteCount = vote_count.toInt(),
        episodeNumber = episode_number
    )
}
