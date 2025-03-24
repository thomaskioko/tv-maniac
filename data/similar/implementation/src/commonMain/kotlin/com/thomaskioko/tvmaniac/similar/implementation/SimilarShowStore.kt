package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SIMILAR_SHOWS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class SimilarShowStore(
  private val networkDataSource: TmdbShowDetailsNetworkDataSource,
  private val tvShowsDao: TvShowsDao,
  private val similarShowsDao: SimilarShowsDao,
  private val requestManagerRepository: RequestManagerRepository,
  private val formatterUtil: FormatterUtil,
  private val dateFormatter: PlatformDateFormatter,
  private val databaseTransactionRunner: DatabaseTransactionRunner,
  private val dispatchers: AppCoroutineDispatchers,
) : Store<SimilarParams, List<SimilarShows>> by StoreBuilder.from(
  fetcher = Fetcher.of { param: SimilarParams ->
    when (val apiResult = networkDataSource.getSimilarShows(param.showId, param.page)) {
      is ApiResponse.Success -> apiResult.body
      is ApiResponse.Error.GenericError -> {
        throw Throwable("${apiResult.errorMessage}")
      }
      is ApiResponse.Error.HttpError -> {
        throw Throwable("${apiResult.code} - ${apiResult.errorMessage}")
      }
      is ApiResponse.Error.SerializationError -> {
        throw Throwable("${apiResult.errorMessage}")
      }
    }
  },
  sourceOfTruth = SourceOfTruth.of<SimilarParams, TmdbShowResult, List<SimilarShows>>(
    reader = { param: SimilarParams -> similarShowsDao.observeSimilarShows(param.showId) },
    writer = { param: SimilarParams, response ->
      databaseTransactionRunner {
        response.results.forEach { show ->
          tvShowsDao.upsert(
            Tvshow(
              id = Id(show.id.toLong()),
              name = show.name,
              overview = show.overview,
              language = show.originalLanguage,
              status = null,
              first_air_date = show.firstAirDate?.let { dateFormatter.getYear(it) },
              popularity = show.popularity,
              episode_numbers = null,
              last_air_date = null,
              season_numbers = null,
              vote_average = show.voteAverage,
              vote_count = show.voteCount.toLong(),
              genre_ids = show.genreIds,
              poster_path = show.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
              backdrop_path = show.backdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
            ),
          )

          similarShowsDao.upsert(
            similarShowId = show.id.toLong(),
            showId = param.showId,
          )
        }

        requestManagerRepository.upsert(
          entityId = param.showId,
          requestType = SIMILAR_SHOWS.name,
        )
      }
    },
    delete = { param -> similarShowsDao.delete(param.showId) },
    deleteAll = { databaseTransactionRunner(similarShowsDao::deleteAll) },
  )
    .usingDispatchers(
      readDispatcher = dispatchers.databaseRead,
      writeDispatcher = dispatchers.databaseWrite,
    ),
)
  .build()
