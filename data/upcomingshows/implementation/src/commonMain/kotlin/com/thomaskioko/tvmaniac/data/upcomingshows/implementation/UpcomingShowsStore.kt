package com.thomaskioko.tvmaniac.data.upcomingshows.implementation

import com.thomaskioko.tvmaniac.core.db.Tvshows
import com.thomaskioko.tvmaniac.core.db.Upcoming_shows
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.UPCOMING_SHOWS
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import com.thomaskioko.tvmaniac.util.model.ApiResponse
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class UpcomingShowsStore(
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val upcomingShowsDao: UpcomingShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val dateFormatter: PlatformDateFormatter,
    private val scope: AppCoroutineScope,
) : Store<UpcomingParams, List<ShowEntity>> by StoreBuilder.from(
    fetcher = Fetcher.of { params: UpcomingParams ->
        when (
            val response = tmdbRemoteDataSource.getUpComingShows(
                page = params.page,
                firstAirDate = params.startDate,
                lastAirDate = params.endDate,
            )
        ) {
            is ApiResponse.Success -> response.body.results
            is ApiResponse.Error.GenericError -> throw Throwable("${response.errorMessage}")
            is ApiResponse.Error.HttpError ->
                throw Throwable("${response.code} - ${response.errorMessage}")

            is ApiResponse.Error.SerializationError ->
                throw Throwable("${response.errorMessage}")
        }
    },
    sourceOfTruth = SourceOfTruth.Companion.of(
        reader = { _ -> upcomingShowsDao.observeUpcomingShows() },
        writer = { params: UpcomingParams, trendingShows ->

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

                upcomingShowsDao.upsert(
                    Upcoming_shows(
                        id = Id(show.id.toLong()),
                        page = Id(params.page),
                    ),
                )
            }

            requestManagerRepository.insert(
                entityId = params.page,
                requestType = UPCOMING_SHOWS.name,
            )
        },
    ),
).scope(scope.io)
    .build()
