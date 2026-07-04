package com.thomaskioko.tvmaniac.presenter.showdetails

import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowDetailsHeaderState
import com.thomaskioko.tvmaniac.presenter.showdetails.model.CastModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ContinueTrackingEpisodeModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ProviderModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.SeasonModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.TrailerModel
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.thomaskioko.tvmaniac.domain.showdetails.model.Casts as DomainCasts
import com.thomaskioko.tvmaniac.domain.showdetails.model.Providers as DomainProviders
import com.thomaskioko.tvmaniac.domain.showdetails.model.Season as DomainSeason
import com.thomaskioko.tvmaniac.domain.showdetails.model.Show as DomainShow
import com.thomaskioko.tvmaniac.domain.showdetails.model.ShowDetails as DomainShowDetails
import com.thomaskioko.tvmaniac.domain.showdetails.model.Trailer as DomainTrailer

internal fun DomainShowDetails.toHeaderState(localizer: Localizer): ShowDetailsHeaderState =
    ShowDetailsHeaderState(
        tmdbId = tmdbId,
        title = title,
        overview = overview,
        language = language,
        posterImageUrl = posterImageUrl,
        backdropImageUrl = backdropImageUrl,
        year = year,
        status = status?.localizeStatus(localizer),
        votes = votes,
        rating = rating,
        isInLibrary = isInLibrary,
        genres = genres.toImmutableList(),
    )

internal fun List<DomainCasts>.toCastModels(): ImmutableList<CastModel> =
    map {
        CastModel(
            id = it.id,
            name = it.name,
            profileUrl = it.profileUrl,
            characterName = it.characterName,
        )
    }.toImmutableList()

internal fun List<DomainProviders>.toProviderModels(): ImmutableList<ProviderModel> =
    map {
        ProviderModel(
            id = it.id,
            name = it.name,
            logoUrl = it.logoUrl,
        )
    }.toImmutableList()

internal fun List<DomainSeason>.toSeasonModels(): ImmutableList<SeasonModel> =
    map {
        SeasonModel(
            seasonId = it.seasonId,
            tvShowId = it.tvShowId,
            name = it.name,
            seasonNumber = it.seasonNumber,
            watchedCount = it.watchedCount,
            totalCount = it.totalCount,
        )
    }.toImmutableList()

internal fun List<DomainShow>.toShowModels(): ImmutableList<ShowModel> =
    map {
        ShowModel(
            showId = it.showId,
            title = it.title,
            posterImageUrl = it.posterImageUrl,
            backdropImageUrl = it.backdropImageUrl,
            isInLibrary = it.isInLibrary,
        )
    }.toImmutableList()

internal fun List<DomainTrailer>.toTrailerModels(): ImmutableList<TrailerModel> =
    map {
        TrailerModel(
            showId = it.showId,
            key = it.key,
            name = it.name,
            youtubeThumbnailUrl = it.youtubeThumbnailUrl,
        )
    }.toImmutableList()

internal fun ImmutableList<EpisodeDetails>.toContinueTrackingModels(showId: Long): ImmutableList<ContinueTrackingEpisodeModel> =
    map { it.toContinueTrackingModel(showId) }.toImmutableList()

internal fun ImmutableList<EpisodeDetails>.toScrollIndex(): Int {
    val firstUnwatched = indexOfFirst { !it.isWatched }
    if (firstUnwatched >= 0) return firstUnwatched

    val nextAfterLastWatched = indexOfLast { it.isWatched } + 1
    return if (nextAfterLastWatched < size) nextAfterLastWatched else 0
}

private fun EpisodeDetails.toContinueTrackingModel(showId: Long): ContinueTrackingEpisodeModel {
    val seasonStr = "S${seasonNumber.toString().padStart(2, '0')}"
    val episodeStr = "E${episodeNumber.toString().padStart(2, '0')}"
    return ContinueTrackingEpisodeModel(
        episodeId = id,
        seasonId = seasonId,
        showId = showId,
        episodeNumber = episodeNumber,
        seasonNumber = seasonNumber,
        episodeNumberFormatted = "$seasonStr | $episodeStr",
        episodeTitle = name,
        imageUrl = stillPath,
        isWatched = isWatched,
        daysUntilAir = daysUntilAir,
        hasAired = hasAired,
    )
}

private fun String.localizeStatus(localizer: Localizer): String {
    val key = when (lowercase()) {
        "returning series" -> StringResourceKey.LabelLibraryStatusReturning
        "planned" -> StringResourceKey.LabelLibraryStatusPlanned
        "in production" -> StringResourceKey.LabelLibraryStatusInProduction
        "ended" -> StringResourceKey.LabelLibraryStatusEnded
        "canceled", "cancelled" -> StringResourceKey.LabelLibraryStatusCanceled
        else -> return capitalizeFirstCharacter()
    }
    return localizer.getString(key)
}

private fun String.capitalizeFirstCharacter(): String = replaceFirstChar { char -> char.uppercase() }
