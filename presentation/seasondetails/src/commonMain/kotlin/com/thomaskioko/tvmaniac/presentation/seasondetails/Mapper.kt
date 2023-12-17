package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.thomaskioko.tvmaniac.core.db.Season_cast
import com.thomaskioko.tvmaniac.core.db.Season_images
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.Cast
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.EpisodeDetailsModel
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonImagesModel
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

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

fun List<Season_images>.toImageList(): PersistentList<SeasonImagesModel> =
    map {
        SeasonImagesModel(
            id = it.id,
            imageUrl = it.image_url,
        )
    }.toPersistentList()

fun List<Season_cast>.toCastList(): PersistentList<Cast> =
    map {
        Cast(
            id = it.id.id,
            name = it.name,
            profileUrl = it.profile_path,
            characterName = it.character_name,
        )
    }.toPersistentList()
