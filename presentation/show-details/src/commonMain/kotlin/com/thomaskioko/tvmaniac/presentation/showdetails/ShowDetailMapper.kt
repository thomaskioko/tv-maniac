package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.core.db.RecommendedShows
import com.thomaskioko.tvmaniac.core.db.ShowCast
import com.thomaskioko.tvmaniac.core.db.ShowSeasons
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.core.db.WatchProviders
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Casts
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Providers
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowDetails
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal fun Either<Failure, List<ShowSeasons>>.toSeasonsListOrEmpty(): ImmutableList<Season> =
  (this as? Either.Right)?.right?.toSeasonsList() ?: persistentListOf()

internal fun Either<Failure, List<WatchProviders>>.toWatchProviderListOrEmpty():
  ImmutableList<Providers> =
  (this as? Either.Right)?.right?.toWatchProviderList() ?: persistentListOf()

internal fun Either<Failure, List<SimilarShows>>.toSimilarShowListOrEmpty(): ImmutableList<Show> =
  (this as? Either.Right)?.right?.toSimilarShowList() ?: persistentListOf()

internal fun Either<Failure, List<RecommendedShows>>.toRecommendedShowListOrEmpty():
  ImmutableList<Show> =
  (this as? Either.Right)?.right?.toRecommendedShowList() ?: persistentListOf()

internal fun Either<Failure, List<Trailers>>.toTrailerListOrEmpty(): ImmutableList<Trailer> =
  (this as? Either.Right)?.right?.toTrailerList() ?: persistentListOf()

internal fun List<ShowCast>?.toCastList(): ImmutableList<Casts> =
  this?.map {
      Casts(
        id = it.id.id,
        name = it.name,
        profileUrl = it.profile_path,
        characterName = it.character_name,
      )
    }
    ?.toImmutableList()
    ?: persistentListOf()

private fun List<SimilarShows>?.toSimilarShowList(): ImmutableList<Show> =
  this?.map {
      Show(
        tmdbId = it.id.id,
        title = it.name,
        posterImageUrl = it.poster_path,
        backdropImageUrl = it.backdrop_path,
        isInLibrary = it.in_library == 1L,
      )
    }
    ?.toImmutableList()
    ?: persistentListOf()

private fun List<RecommendedShows>?.toRecommendedShowList(): ImmutableList<Show> =
  this?.map {
      Show(
        tmdbId = it.id.id,
        title = it.name,
        posterImageUrl = it.poster_path,
        backdropImageUrl = it.backdrop_path,
        isInLibrary = it.in_library == 1L,
      )
    }
    ?.toImmutableList()
    ?: persistentListOf()

private fun List<WatchProviders>?.toWatchProviderList(): ImmutableList<Providers> =
  this?.map {
      Providers(
        id = it.id.id,
        name = it.name ?: "",
        logoUrl = it.logo_path,
      )
    }
    ?.toImmutableList()
    ?: persistentListOf()

internal fun TvshowDetails.toShowDetails(): ShowDetails =
  ShowDetails(
    tmdbId = id.id,
    title = name,
    overview = overview,
    language = language,
    posterImageUrl = poster_path,
    backdropImageUrl = backdrop_path,
    votes = vote_count,
    rating = vote_average,
    genres = genre_list?.split(", ")?.toImmutableList() ?: persistentListOf(),
    year = last_air_date ?: first_air_date ?: "",
    status = status,
    isFollowed = in_library == 1L,
  )

private fun List<ShowSeasons>?.toSeasonsList(): ImmutableList<Season> =
  this?.map {
      Season(
        seasonId = it.season_id.id,
        tvShowId = it.show_id.id,
        name = it.season_title,
        seasonNumber = it.season_number,
      )
    }
    ?.toImmutableList()
    ?: persistentListOf()

private fun List<Trailers>?.toTrailerList(): ImmutableList<Trailer> =
  this?.map {
      Trailer(
        showId = it.show_id.id,
        key = it.key,
        name = it.name,
        youtubeThumbnailUrl = "https://i.ytimg.com/vi/${it.key}/hqdefault.jpg",
      )
    }
    ?.toImmutableList()
    ?: persistentListOf()
