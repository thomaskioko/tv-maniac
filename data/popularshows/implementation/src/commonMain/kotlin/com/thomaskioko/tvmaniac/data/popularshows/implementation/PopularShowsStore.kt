package com.thomaskioko.tvmaniac.data.popularshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.db.Popular_shows
import com.thomaskioko.tvmaniac.core.db.Tvshows
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.paging.CACHE_EXPIRE_TIME
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.POPULAR_SHOWS
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MemoryPolicy
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class PopularShowsStore(
  private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
  private val requestManagerRepository: RequestManagerRepository,
  private val dateFormatter: PlatformDateFormatter,
  private val popularShowsDao: PopularShowsDao,
  private val tvShowsDao: TvShowsDao,
  private val formatterUtil: FormatterUtil,
) :
  Store<Long, List<ShowEntity>> by StoreBuilder.from(
      fetcher =
        Fetcher.of { page ->
          when (val response = tmdbRemoteDataSource.getPopularShows(page = page)) {
            is ApiResponse.Success -> response.body.results
            is ApiResponse.Error.GenericError -> throw Throwable("${response.errorMessage}")
            is ApiResponse.Error.HttpError ->
              throw Throwable("${response.code} - ${response.errorMessage}")
            is ApiResponse.Error.SerializationError -> throw Throwable("${response.errorMessage}")
          }
        },
      sourceOfTruth =
        SourceOfTruth.of(
          reader = { page -> popularShowsDao.observePopularShows(page) },
          writer = { page, trendingShows ->
            trendingShows.forEach { show ->
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

              popularShowsDao.upsert(
                Popular_shows(
                  id = Id(show.id.toLong()),
                  page = Id(page),
                ),
              )
            }

            requestManagerRepository.upsert(
              entityId = page,
              requestType = POPULAR_SHOWS.name,
            )
          },
          delete = popularShowsDao::deletePopularShow,
          deleteAll = popularShowsDao::deletePopularShows,
        ),
    )
    .cachePolicy(
      MemoryPolicy.builder<Long, List<ShowEntity>>()
        .setExpireAfterWrite(CACHE_EXPIRE_TIME)
        .build()
    )
    .build()
