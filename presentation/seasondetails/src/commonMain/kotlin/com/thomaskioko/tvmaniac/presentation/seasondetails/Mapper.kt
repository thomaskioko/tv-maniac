package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.thomaskioko.tvmaniac.core.db.SeasonEpisodeDetailsById
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.Episode
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetails

fun List<SeasonEpisodeDetailsById>?.toSeasonWithEpisodes(): List<SeasonDetails> {
    return this?.groupBy { it.season_id }?.map { (_, groupMap) ->
        val seasonRow = groupMap.first()
        SeasonDetails(
            seasonId = seasonRow.season_id.id,
            seasonName = seasonRow.season_title,
            episodes = groupMap.map { it.toEpisode() },
            episodeCount = seasonRow.episode_count,
            watchProgress = 0f,
        )
    } ?: emptyList()
}

fun SeasonEpisodeDetailsById.toEpisode(): Episode {
    return Episode(
        id = episode_id.id,
        seasonId = season_id.id,
        episodeTitle = episode_title,
        episodeNumberTitle = "E$episode_number • $episode_title",
        overview = overview,
        imageUrl = episode_image_url,
        runtime = runtime,
        voteCount = votes,
        episodeNumber = episode_number,
        seasonEpisodeNumber = "S${
            season_number
                .toString()
                .padStart(2, '0')
        } | E$episode_number",
    )
}

fun List<SeasonEpisodeDetailsById>?.getTitle(): String = this?.firstOrNull()?.show_title ?: ""
