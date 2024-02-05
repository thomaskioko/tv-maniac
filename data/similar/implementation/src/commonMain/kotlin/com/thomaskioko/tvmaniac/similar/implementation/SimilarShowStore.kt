package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.db.Tvshows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SIMILAR_SHOWS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import com.thomaskioko.tvmaniac.util.model.ApiResponse
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
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
  private val scope: AppCoroutineScope,
  private val logger: KermitLogger,
) :
  Store<SimilarParams, List<SimilarShows>> by StoreBuilder.from(
      fetcher =
        Fetcher.of { param: SimilarParams ->
          when (val apiResult = networkDataSource.getSimilarShows(param.showId, param.page)) {
            is ApiResponse.Success -> apiResult.body
            is ApiResponse.Error.GenericError -> {
              logger.error("SimilarShowStore GenericError", "${apiResult.errorMessage}")
              throw Throwable("${apiResult.errorMessage}")
            }
            is ApiResponse.Error.HttpError -> {
              logger.error(
                "SimilarShowStore HttpError",
                "${apiResult.code} - ${apiResult.errorBody}",
              )
              throw Throwable("${apiResult.code} - ${apiResult.errorMessage}")
            }
            is ApiResponse.Error.SerializationError -> {
              logger.error(
                "SimilarShowStore SerializationError",
                "${apiResult.errorMessage}",
              )
              throw Throwable("${apiResult.errorMessage}")
            }
          }
        },
      sourceOfTruth =
        SourceOfTruth.of(
          reader = { param: SimilarParams -> similarShowsDao.observeSimilarShows(param.showId) },
          writer = { param: SimilarParams, response ->
            response.results.forEach { show ->
              tvShowsDao.upsert(
                Tvshows(
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

            requestManagerRepository.insert(
              entityId = param.showId,
              requestType = SIMILAR_SHOWS.name,
            )
          },
          delete = { param -> similarShowsDao.delete(param.showId) },
          deleteAll = similarShowsDao::deleteAll,
        ),
    )
    .scope(scope.io)
    .build()
