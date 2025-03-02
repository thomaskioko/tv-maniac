package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Featured_shows
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsDao
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.FEATURED_SHOWS_TODAY
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

private const val FEATURED_SHOWS_COUNT = 5

@Inject
class FeaturedShowsStore(
  private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
  private val requestManagerRepository: RequestManagerRepository,
  private val featuredShowsDao: FeaturedShowsDao,
  private val tvShowsDao: TvShowsDao,
  private val formatterUtil: FormatterUtil,
  private val dateFormatter: PlatformDateFormatter,
  private val databaseTransactionRunner: DatabaseTransactionRunner,
  private val dispatcher: AppCoroutineDispatchers
) :
  Store<Long, List<ShowEntity>> by StoreBuilder.from(
      fetcher =
        Fetcher.of { page ->
          when (val response = tmdbRemoteDataSource.discoverShows(page = page)) {
            is ApiResponse.Success -> response.body.results
            is ApiResponse.Error.GenericError -> throw Throwable("${response.errorMessage}")
            is ApiResponse.Error.HttpError ->
              throw Throwable("${response.code} - ${response.errorMessage}")
            is ApiResponse.Error.SerializationError -> throw Throwable("${response.errorMessage}")
          }
        },
      sourceOfTruth =
        SourceOfTruth.Companion.of(
          reader = { page: Long -> featuredShowsDao.observeFeaturedShows(page) },
          writer = { _, shows ->
            withContext(dispatcher.io) {
              databaseTransactionRunner {
                featuredShowsDao.deleteFeaturedShows()
                shows.shuffled().take(FEATURED_SHOWS_COUNT).forEach { show ->
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
                      backdrop_path =
                        show.backdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                    ),
                  )
                  featuredShowsDao.upsert(
                    Featured_shows(
                      id = Id(show.id.toLong()),
                    ),
                  )
                }

                requestManagerRepository.upsert(
                  entityId = FEATURED_SHOWS_TODAY.requestId,
                  requestType = FEATURED_SHOWS_TODAY.name,
                )
              }
            }
          },
        ),
    )
    .build()
