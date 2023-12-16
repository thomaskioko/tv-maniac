package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.thomaskioko.tvmaniac.presentation.seasondetails.model.EpisodeDetailsModel
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsModel
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

fun SeasonDetailsWithEpisodes.toSeasonDetails(): SeasonDetailsModel {
    return SeasonDetailsModel(
        seasonId = seasonId,
        seasonName = name,
        episodeDetailModels = episodes.toEpisodes(),
        episodeCount = episodeCount,
        watchProgress = 0f,
    )
}

fun List<EpisodeDetails>.toEpisodes(): PersistentList<EpisodeDetailsModel> = map {
    EpisodeDetailsModel(
        id = it.id,
        seasonId = it.seasonId,
        episodeTitle = it.name,
        episodeNumberTitle = "E${it.episodeNumber} • ${it.name}",
        overview = it.overview,
        imageUrl = it.stillPath,
        runtime = it.runtime,
        voteCount = it.voteCount,
        episodeNumber = it.episodeNumber.toString(),
        seasonEpisodeNumber = "S${
            it.seasonNumber
                .toString()
                .padStart(2, '0')
        } | E${it.episodeNumber}",
    )
}.toPersistentList()
