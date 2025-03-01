package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.db.RecommendedShows
import com.thomaskioko.tvmaniac.db.ShowCast
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.db.Trailers
import com.thomaskioko.tvmaniac.db.TvshowDetails
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Casts
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Providers
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowDetails
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import com.thomaskioko.tvmaniac.util.FormatterUtil
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import me.tatarka.inject.annotations.Inject

@Inject
class ShowDetailsMapper(
  private val formatterUtil: FormatterUtil,
) {

  fun toShowDetails(show: TvshowDetails): ShowDetails =
    ShowDetails(
      tmdbId = show.id.id,
      title = show.name,
      overview = show.overview,
      language = show.language,
      posterImageUrl = show.poster_path,
      backdropImageUrl = show.backdrop_path,
      votes = show.vote_count,
      rating = formatterUtil.formatDouble(show.vote_average, 1),
      genres = show.genre_list?.split(", ")?.toImmutableList() ?: persistentListOf(),
      year = show.last_air_date ?: show.first_air_date ?: "",
      status = show.status,
      isFollowed = show.in_library == 1L,
    )

}

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
