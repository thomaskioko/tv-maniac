package com.thomaskioko.tvmaniac.data.recommendedshows.implementation

import com.thomaskioko.tvmaniac.core.db.RecommendedShows
import com.thomaskioko.tvmaniac.core.db.Tvshows
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsDao
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsParams
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.RECOMMENDED_SHOWS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
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
class RecommendedShowStore(
  private val networkDataSource: TmdbShowDetailsNetworkDataSource,
  private val tvShowsDao: TvShowsDao,
  private val recommendedShowsDao: RecommendedShowsDao,
  private val requestManagerRepository: RequestManagerRepository,
  private val formatterUtil: FormatterUtil,
  private val dateFormatter: PlatformDateFormatter,
  private val scope: AppCoroutineScope,
  private val logger: KermitLogger,
) :
  Store<RecommendedShowsParams, List<RecommendedShows>> by StoreBuilder.from(
      fetcher =
        Fetcher.of { param: RecommendedShowsParams ->
          when (val apiResult = networkDataSource.getRecommendedShows(param.showId, param.page)) {
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
          reader = { param: RecommendedShowsParams ->
            recommendedShowsDao.observeRecommendedShows(
              param.showId,
            )
          },
          writer = { param: RecommendedShowsParams, response ->
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

              recommendedShowsDao.upsert(
                recommendedShowId = show.id.toLong(),
                showId = param.showId,
              )
            }

            requestManagerRepository.insert(
              entityId = param.showId,
              requestType = RECOMMENDED_SHOWS.name,
            )
          },
          delete = { param -> recommendedShowsDao.delete(param.showId) },
          deleteAll = recommendedShowsDao::deleteAll,
        ),
    )
    .scope(scope.io)
    .build()
