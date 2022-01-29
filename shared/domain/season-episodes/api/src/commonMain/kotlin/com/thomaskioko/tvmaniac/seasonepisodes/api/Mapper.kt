package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.seasonepisodes.api.model.Episode
import com.thomaskioko.tvmaniac.seasonepisodes.api.model.SeasonWithEpisodes

fun List<SelectSeasonWithEpisodes>.toSeasonWithEpisodes(): List<SeasonWithEpisodes> {
    return groupBy { it.name }.map { groupMap ->
        SeasonWithEpisodes(
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
        episodeTitle = name_,
        episodeNumberTitle = "E$episode_number • $name_",
        overview = overview_,
        imageUrl = image_url,
        voteAverage = vote_average_,
        voteCount = vote_count.toInt(),
        episodeNumber = episode_number,
        seasonEpisodeNumber = "S${
        season_number
            .toString()
            .padStart(2, '0')
        } | E$episode_number"
    )
}
