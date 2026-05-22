package com.thomaskioko.tvmaniac.presenter.showdetails

import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.presenter.showdetails.model.CastModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ContinueTrackingEpisodeModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ProviderModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.SeasonModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowDetailsModel
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
import com.thomaskioko.tvmaniac.domain.showdetails.model.ShowMetadata as DomainShowMetadata
import com.thomaskioko.tvmaniac.domain.showdetails.model.Trailer as DomainTrailer

internal fun ShowDetailsModel.applyShowDetails(
    details: DomainShowDetails,
    localizer: Localizer,
): ShowDetailsModel = copy(
    tmdbId = details.tmdbId,
    title = details.title,
    overview = details.overview,
    language = details.language,
    posterImageUrl = details.posterImageUrl,
    backdropImageUrl = details.backdropImageUrl,
    votes = details.votes,
    rating = details.rating,
    year = details.year,
    status = details.status?.localizeStatus(localizer),
    isInLibrary = details.isInLibrary,
    genres = details.genres.toImmutableList(),
)

internal fun ShowDetailsModel.applyMetadata(metadata: DomainShowMetadata): ShowDetailsModel = copy(
    hasWebViewInstalled = metadata.hasWebViewInstalled,
    numberOfSeasons = metadata.seasonsList.size,
    providers = metadata.providers.toWatchProviderList(),
    castsList = metadata.castsList.toCastList(),
    seasonsList = metadata.seasonsList.toSeasonsList(),
    similarShows = metadata.similarShows.toShowList(),
    trailersList = metadata.trailersList.toTrailerList(),
)

internal fun ShowDetailsModel.applyWatchProgress(progress: ShowWatchProgress): ShowDetailsModel = copy(
    watchedEpisodesCount = progress.watchedCount,
    totalEpisodesCount = progress.totalCount,
    watchProgress = progress.progressPercentage,
)

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

internal fun List<DomainCasts>.toCastList(): ImmutableList<CastModel> =
    this.map {
        CastModel(
            id = it.id,
            name = it.name,
            profileUrl = it.profileUrl,
            characterName = it.characterName,
        )
    }.toImmutableList()

internal fun List<DomainShow>.toShowList(): ImmutableList<ShowModel> =
    this.map {
        ShowModel(
            traktId = it.traktId,
            title = it.title,
            posterImageUrl = it.posterImageUrl,
            backdropImageUrl = it.backdropImageUrl,
            isInLibrary = it.isInLibrary,
        )
    }.toImmutableList()

internal fun List<DomainProviders>.toWatchProviderList(): ImmutableList<ProviderModel> =
    this.map {
        ProviderModel(
            id = it.id,
            name = it.name,
            logoUrl = it.logoUrl,
        )
    }.toImmutableList()

internal fun List<DomainSeason>.toSeasonsList(): ImmutableList<SeasonModel> =
    this.map {
        SeasonModel(
            seasonId = it.seasonId,
            tvShowId = it.tvShowId,
            name = it.name,
            seasonNumber = it.seasonNumber,
            watchedCount = it.watchedCount,
            totalCount = it.totalCount,
        )
    }.toImmutableList()

internal fun List<DomainTrailer>.toTrailerList(): ImmutableList<TrailerModel> =
    this.map {
        TrailerModel(
            showTmdbId = it.showTmdbId,
            key = it.key,
            name = it.name,
            youtubeThumbnailUrl = it.youtubeThumbnailUrl,
        )
    }.toImmutableList()

internal fun EpisodeDetails.toContinueTrackingModel(showTraktId: Long): ContinueTrackingEpisodeModel {
    val seasonStr = "S${seasonNumber.toString().padStart(2, '0')}"
    val episodeStr = "E${episodeNumber.toString().padStart(2, '0')}"
    return ContinueTrackingEpisodeModel(
        episodeId = id,
        seasonId = seasonId,
        showTraktId = showTraktId,
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

internal fun mapContinueTrackingEpisodes(
    episodes: ImmutableList<EpisodeDetails>,
    showTraktId: Long,
): ImmutableList<ContinueTrackingEpisodeModel> {
    return episodes
        .map { it.toContinueTrackingModel(showTraktId) }
        .toImmutableList()
}

private fun String.capitalizeFirstCharacter(): String = replaceFirstChar { char -> char.uppercase() }
