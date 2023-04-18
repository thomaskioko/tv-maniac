package com.thomaskioko.tvmaniac.domain.seasondetails

import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.domain.seasondetails.model.Episode
import com.thomaskioko.tvmaniac.domain.seasondetails.model.SeasonDetails

fun Either.Right<List<SelectSeasonWithEpisodes>>.toSeasonWithEpisodes(): List<SeasonDetails> {
    return data?.groupBy { it.name }?.map { groupMap ->
        SeasonDetails(
            seasonId = groupMap.value.first().season_id,
            seasonName = groupMap.key,
            episodes = groupMap.value.map { it.toEpisode() },
            episodeCount = groupMap.value.size.toLong(),
            watchProgress = 0f // TODO:: Fetch watch progress
        )
    } ?: emptyList()
}

fun List<SelectSeasonWithEpisodes>?.toSeasonWithEpisodes(): List<SeasonDetails> {
    return this?.groupBy { it.name }?.map { groupMap ->
        SeasonDetails(
            seasonId = groupMap.value.first().season_id,
            seasonName = groupMap.key,
            episodes = groupMap.value.map { it.toEpisode() },
            episodeCount = groupMap.value.size.toLong(),
            watchProgress = 0f // TODO:: Fetch watch progress
        )
    } ?: emptyList()
}

fun SelectSeasonWithEpisodes.toEpisode(): Episode {
    return Episode(
        id = id,
        seasonId = season_id,
        episodeTitle = title_,
        episodeNumberTitle = "E${episode_number} • $title_",
        overview = overview,
        imageUrl = image_url,
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

fun Either.Right<List<SelectSeasonWithEpisodes>>.getTitle(): String =
    data?.firstOrNull()?.title ?: ""

fun List<SelectSeasonWithEpisodes>?.getTitle(): String =
    this?.firstOrNull()?.title ?: ""


