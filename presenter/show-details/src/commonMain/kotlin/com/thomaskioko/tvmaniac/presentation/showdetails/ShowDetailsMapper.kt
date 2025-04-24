package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.presentation.showdetails.model.CastModel
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ProviderModel
import com.thomaskioko.tvmaniac.presentation.showdetails.model.SeasonModel
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowModel
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowDetailsModel
import com.thomaskioko.tvmaniac.presentation.showdetails.model.TrailerModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.thomaskioko.tvmaniac.domain.showdetails.model.Casts as DomainCasts
import com.thomaskioko.tvmaniac.domain.showdetails.model.Providers as DomainProviders
import com.thomaskioko.tvmaniac.domain.showdetails.model.Season as DomainSeason
import com.thomaskioko.tvmaniac.domain.showdetails.model.Show as DomainShow
import com.thomaskioko.tvmaniac.domain.showdetails.model.ShowDetails as DomainShowDetails
import com.thomaskioko.tvmaniac.domain.showdetails.model.Trailer as DomainTrailer

fun DomainShowDetails.toShowDetails(): ShowDetailsModel = ShowDetailsModel(
    tmdbId = tmdbId,
    title = title,
    overview = overview,
    language = language,
    posterImageUrl = posterImageUrl,
    backdropImageUrl = backdropImageUrl,
    votes = votes,
    rating = rating,
    year = year,
    status = status,
    isInLibrary = isInLibrary,
    hasWebViewInstalled = hasWebViewInstalled,
    numberOfSeasons = numberOfSeasons,
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
            tmdbId = it.tmdbId,
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
        )
    }.toImmutableList()


internal fun List<DomainTrailer>.toTrailerList(): ImmutableList<TrailerModel> =
    this.map {
        TrailerModel(
            showId = it.showId,
            key = it.key,
            name = it.name,
            youtubeThumbnailUrl = it.youtubeThumbnailUrl,
        )
    }.toImmutableList()
