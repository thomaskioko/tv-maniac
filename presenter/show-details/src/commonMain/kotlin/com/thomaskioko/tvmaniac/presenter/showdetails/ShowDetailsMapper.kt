package com.thomaskioko.tvmaniac.presenter.showdetails

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
import com.thomaskioko.tvmaniac.domain.showdetails.model.Trailer as DomainTrailer

public fun DomainShowDetails.toShowDetails(
    watchedEpisodesCount: Int = 0,
    totalEpisodesCount: Int = 0,
    watchProgress: Float = 0f,
): ShowDetailsModel = ShowDetailsModel(
    tmdbId = tmdbId,
    title = title,
    overview = overview,
    language = language,
    posterImageUrl = posterImageUrl,
    backdropImageUrl = backdropImageUrl,
    votes = votes,
    rating = rating,
    year = year,
    status = status?.capitalizeFirstCharacter(),
    isInLibrary = isInLibrary,
    hasWebViewInstalled = hasWebViewInstalled,
    numberOfSeasons = numberOfSeasons ?: 0,
    watchedEpisodesCount = watchedEpisodesCount,
    totalEpisodesCount = totalEpisodesCount,
    watchProgress = watchProgress,
    genres = genres.toImmutableList(),
    seasonsList = seasonsList.toSeasonsList(),
    providers = providers.toWatchProviderList(),
    castsList = castsList.toCastList(),
    similarShows = similarShows.toShowList(),
    recommendedShows = recommendedShows.toShowList(),
    trailersList = trailersList.toTrailerList(),
)

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
