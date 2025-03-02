package com.thomaskioko.tvmaniac.search.implementation

import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.util.FormatterUtil
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class SearchShowStore(
  private val tvShowsDao: TvShowsDao,
  private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
  private val formatterUtil: FormatterUtil,
) :
  Store<String, List<ShowEntity>> by StoreBuilder.from(
    fetcher =
    Fetcher.of { query: String ->
      when (val response = tmdbRemoteDataSource.searchShows(query)) {
        is ApiResponse.Success -> response.body.results
        is ApiResponse.Error.GenericError -> throw Throwable("${response.errorMessage}")
        is ApiResponse.Error.HttpError ->
          throw Throwable("${response.code} - ${response.errorMessage}")
        is ApiResponse.Error.SerializationError -> throw Throwable("${response.errorMessage}")
      }
    },
    sourceOfTruth =
    SourceOfTruth.Companion.of(
      reader = { query: String -> tvShowsDao.observeShowsByQuery(query) },
      writer = { _, shows ->
        shows.forEach { show ->
          tvShowsDao.upsert(
            Tvshow(
              id = Id(show.id.toLong()),
              name = show.name,
              overview = show.overview,
              language = show.originalLanguage,
              popularity = show.popularity,
              vote_average = show.voteAverage,
              vote_count = show.voteCount.toLong(),
              genre_ids = show.genreIds,
              poster_path = show.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
              backdrop_path =
              show.backdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
              status = null,
              first_air_date = null,
              episode_numbers = null,
              last_air_date = null,
              season_numbers = null,
            ),
          )
        }
      },
    ),
  )
    .build()
