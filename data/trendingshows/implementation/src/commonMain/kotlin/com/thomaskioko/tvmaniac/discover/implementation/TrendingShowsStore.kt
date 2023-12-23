package com.thomaskioko.tvmaniac.discover.implementation

import com.thomaskioko.tvmaniac.core.db.Trending_shows
import com.thomaskioko.tvmaniac.core.db.Tvshows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsDao
import com.thomaskioko.tvmaniac.resourcemanager.api.LastRequest
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.Category
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import com.thomaskioko.tvmaniac.util.model.ApiResponse
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class TrendingShowsStore(
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val trendingShowsDao: TrendingShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val dateFormatter: PlatformDateFormatter,
    private val scope: AppCoroutineScope,
) : Store<String, List<ShowEntity>> by StoreBuilder.from(
    fetcher = Fetcher.of { timeWindow ->
        when (val response = tmdbRemoteDataSource.getTrendingShows(timeWindow)) {
            is ApiResponse.Success -> {
                response.body.results
            }

            is ApiResponse.Error.GenericError -> {
                throw Throwable("${response.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                throw Throwable("${response.code} - ${response.errorMessage}")
            }

            is ApiResponse.Error.SerializationError -> {
                throw Throwable("${response.errorMessage}")
            }
        }
    },
    sourceOfTruth = SourceOfTruth.Companion.of(
        reader = { _: String ->
            trendingShowsDao.observeTvShow()
                .map { shows ->
                    shows.map { show ->
                        ShowEntity(
                            id = show.id.id,
                            title = show.name,
                            posterPath = show.poster_path,
                            inLibrary = show.in_library == 1L,
                        )
                    }
                }
        },
        writer = { _, trendingShows ->
            trendingShows.forEach { show ->
                tvShowsDao.upsert(
                    Tvshows(
                        id = Id(show.id.toLong()),
                        name = show.name,
                        overview = show.overview,
                        language = show.originalLanguage,
                        status = null,
                        first_air_date = show.firstAirDate?.let {
                            dateFormatter.getYear(it)
                        },
                        popularity = show.popularity,
                        episode_numbers = null,
                        last_air_date = null,
                        season_numbers = null,
                        vote_average = show.voteAverage,
                        vote_count = show.voteCount.toLong(),
                        genre_ids = show.genreIds,
                        poster_path = show.posterPath?.let {
                            formatterUtil.formatTmdbPosterPath(it)
                        },
                        backdrop_path = show.backdropPath?.let {
                            formatterUtil.formatTmdbPosterPath(it)
                        },
                    ),
                )

                trendingShowsDao.upsert(
                    Trending_shows(
                        id = Id(show.id.toLong()),
                        page = Id(DEFAULT_API_PAGE),
                    ),
                )
            }
            requestManagerRepository.insert(
                LastRequest(
                    id = Category.TRENDING_TODAY.id,
                    entityId = Category.TRENDING_TODAY.id,
                    requestType = Category.TRENDING_TODAY.name,
                ),
            )
        },
    ),
).scope(scope.io)
    .build()
