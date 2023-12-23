package com.thomaskioko.tvmaniac.toprated.data.implementation

import com.thomaskioko.tvmaniac.core.db.Toprated_shows
import com.thomaskioko.tvmaniac.core.db.Tvshows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.resourcemanager.api.LastRequest
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.Category
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsDao
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
class TopRatedShowsStore(
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val topRatedShowsDao: TopRatedShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val dateFormatter: PlatformDateFormatter,
    private val scope: AppCoroutineScope,
) : Store<Long, List<ShowEntity>> by StoreBuilder.from(
    fetcher = Fetcher.of { page ->
        when (val response = tmdbRemoteDataSource.getTopRatedShows(page = page)) {
            is ApiResponse.Success -> response.body.results
            is ApiResponse.Error.GenericError -> throw Throwable("${response.errorMessage}")
            is ApiResponse.Error.HttpError ->
                throw Throwable("${response.code} - ${response.errorMessage}")

            is ApiResponse.Error.SerializationError ->
                throw Throwable("${response.errorMessage}")
        }
    },
    sourceOfTruth = SourceOfTruth.Companion.of(
        reader = { page: Long ->
            topRatedShowsDao.observeTrendingShows(page)
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
        writer = { page, trendingShows ->

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

                topRatedShowsDao.upsert(
                    Toprated_shows(
                        id = Id(show.id.toLong()),
                        page = Id(page),
                    ),
                )
            }

            requestManagerRepository.insert(
                LastRequest(
                    id = Category.TOP_RATED.id,
                    entityId = page,
                    requestType = Category.TOP_RATED.name,
                ),
            )
        },
    ),
).scope(scope.io)
    .build()
