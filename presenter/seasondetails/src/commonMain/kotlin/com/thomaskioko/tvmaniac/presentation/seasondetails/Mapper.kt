package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.thomaskioko.tvmaniac.domain.seasondetails.model.SeasonCast
import com.thomaskioko.tvmaniac.domain.seasondetails.model.SeasonImages
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.Cast
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.EpisodeDetailsModel
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonImagesModel
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

internal fun List<EpisodeDetails>.toEpisodes(): PersistentList<EpisodeDetailsModel> =
    map {
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
            seasonEpisodeNumber =
            "S${
                it.seasonNumber
                    .toString()
                    .padStart(2, '0')
            } | E${it.episodeNumber}",
        )
    }
        .toPersistentList()

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
