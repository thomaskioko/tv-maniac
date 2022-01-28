package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.seasonepisodes.api.model.Episode
import com.thomaskioko.tvmaniac.seasonepisodes.api.model.SeasonWithEpisodes

fun List<SelectSeasonWithEpisodes>.toSeasonWithEpisodes(): List<SeasonWithEpisodes> {
    return groupBy { it.name }.map { groupMap ->
        SeasonWithEpisodes(
            seasonName = groupMap.key,
            episodes = groupMap.value.map { it.toEpisode() }
        )
    }
}

fun SelectSeasonWithEpisodes.toEpisode(): Episode {
    return Episode(
        id = id,
        seasonId = season_id,
        name = "E$episode_number • $name_",
        overview = overview_,
        imageUrl = image_url,
        voteAverage = vote_average_,
        voteCount = vote_count.toInt(),
        episodeNumber = episode_number
    )
}
