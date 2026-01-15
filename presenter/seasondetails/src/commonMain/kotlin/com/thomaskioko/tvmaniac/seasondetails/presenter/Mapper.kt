package com.thomaskioko.tvmaniac.seasondetails.presenter

import com.thomaskioko.tvmaniac.domain.seasondetails.model.SeasonCast
import com.thomaskioko.tvmaniac.domain.seasondetails.model.SeasonImages
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.Cast
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.EpisodeDetailsModel
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonImagesModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

internal fun List<EpisodeDetails>.toEpisodes(
    updatingEpisodesId: Set<Long> = emptySet(),
): PersistentList<EpisodeDetailsModel> {
    val sortedEpisodes = this.sortedBy { it.episodeNumber }
    return sortedEpisodes.mapIndexed { index, episode ->
        val hasPreviousUnwatched = sortedEpisodes.take(index).any { prev ->
            !prev.isWatched && prev.hasAired
        }
        EpisodeDetailsModel(
            id = episode.id,
            seasonId = episode.seasonId,
            episodeTitle = episode.name,
            episodeNumberTitle = "E${episode.episodeNumber} • ${episode.name}",
            overview = episode.overview,
            imageUrl = episode.stillPath,
            runtime = episode.runtime,
            voteCount = episode.voteCount,
            episodeNumber = episode.episodeNumber,
            seasonNumber = episode.seasonNumber,
            seasonEpisodeNumber =
            "S${
                episode.seasonNumber
                    .toString()
                    .padStart(2, '0')
            } | E${episode.episodeNumber}",
            isWatched = episode.isWatched,
            daysUntilAir = episode.daysUntilAir,
            hasAired = episode.hasAired,
            hasPreviousUnwatched = hasPreviousUnwatched,
            isEpisodeUpdating = episode.id in updatingEpisodesId,
        )
    }.toPersistentList()
}

internal fun List<SeasonImages>.toImageList(): PersistentList<SeasonImagesModel> =
    map {
        SeasonImagesModel(
            id = it.id,
            imageUrl = it.imageUrl,
        )
    }
        .toPersistentList()

internal fun List<SeasonCast>.toCastList(): PersistentList<Cast> =
    this.map {
        Cast(
            id = it.id,
            name = it.name,
            profileUrl = it.profilePath,
            characterName = it.characterName,
        )
    }
        .toPersistentList()
