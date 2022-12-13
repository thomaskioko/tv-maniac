package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.util.FormatterUtil
import com.thomaskioko.tvmaniac.seasondetails.api.model.Episode
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetails

fun List<SelectSeasonWithEpisodes>.toSeasonWithEpisodes(): List<SeasonDetails> {
    return groupBy { it.name }.map { groupMap ->
        SeasonDetails(
            seasonId = groupMap.value.first().season_id,
            seasonName = groupMap.key,
            episodes = groupMap.value.map { it.toEpisode() },
            episodeCount = groupMap.value.size,
            watchProgress = 0f // TODO:: Fetch watch progress
        )
    }
}

fun SelectSeasonWithEpisodes.toEpisode(): Episode {
    return Episode(
        id = id,
        seasonId = season_id,
        episodeTitle = name,
        episodeNumberTitle = "E$episode_number • $title_",
        overview = overview__,
        imageUrl = image_url.toImageUrl(),
        runtime = runtime,
        voteCount = votes,
        episodeNumber = episode_number,
        seasonEpisodeNumber = "S${
        season_number
            .toString()
            .padStart(2, '0')
        } | E$episode_number"
    )
}

fun String?.toImageUrl() = FormatterUtil.formatPosterPath(this)
