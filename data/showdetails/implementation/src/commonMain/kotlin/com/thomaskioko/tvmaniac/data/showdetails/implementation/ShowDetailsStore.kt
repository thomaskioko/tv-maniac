package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.core.db.Genres
import com.thomaskioko.tvmaniac.core.db.Networks
import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.Show_networks
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.core.db.Tvshows
import com.thomaskioko.tvmaniac.data.showdetails.api.NetworksDao
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsDao
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerDao
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.resourcemanager.api.LastRequest
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
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
class ShowDetailsStore(
    private val remoteDataSource: TmdbShowDetailsNetworkDataSource,
    private val tvShowsDao: TvShowsDao,
    private val showDetailsDao: ShowDetailsDao,
    private val seasonDao: SeasonsDao,
    private val trailerDao: TrailerDao,
    private val networkDao: NetworksDao,
    private val genreDao: DefaultGenreDao,
    private val formatterUtil: FormatterUtil,
    private val dateFormatter: PlatformDateFormatter,
    private val requestManagerRepository: RequestManagerRepository,
    private val dbTransactionRunner: DatabaseTransactionRunner,
    private val scope: AppCoroutineScope,
) : Store<Long, TvshowDetails> by StoreBuilder.from<Long, TmdbShowDetailsResponse, TvshowDetails>(
    fetcher = Fetcher.of { id ->
        when (val response = remoteDataSource.getShowDetails(id)) {
            is ApiResponse.Success -> response.body
            is ApiResponse.Error.GenericError -> throw Throwable("${response.errorMessage}")
            is ApiResponse.Error.HttpError ->
                throw Throwable("${response.code} - ${response.errorMessage}")

            is ApiResponse.Error.SerializationError ->
                throw Throwable("${response.errorMessage}")
        }
    },
    sourceOfTruth = SourceOfTruth.Companion.of(
        reader = { id -> showDetailsDao.observeTvShows(id) },
        writer = { id, show ->
            dbTransactionRunner {
                tvShowsDao.upsert(
                    Tvshows(
                        id = Id(show.id.toLong()),
                        name = show.name,
                        overview = show.overview,
                        language = show.originalLanguage,
                        status = show.status,
                        first_air_date = show.firstAirDate?.let {
                            dateFormatter.getYear(it)
                        },
                        popularity = show.popularity,
                        episode_numbers = show.numberOfEpisodes.toString(),
                        last_air_date = show.lastAirDate?.let {
                            dateFormatter.getYear(it)
                        },
                        season_numbers = show.numberOfSeasons.toString(),
                        vote_average = show.voteAverage,
                        vote_count = show.voteCount.toLong(),
                        genre_ids = show.genres.map { it.id },
                        poster_path = show.posterPath?.let {
                            formatterUtil.formatTmdbPosterPath(it)
                        },
                        backdrop_path = show.backdropPath?.let {
                            formatterUtil.formatTmdbPosterPath(it)
                        },
                    ),
                )

                // Insert Genres
                show.genres.forEach { genre ->
                    genreDao.upsert(
                        Genres(
                            id = Id(genre.id.toLong()),
                            tmdb_id = Id(show.id.toLong()),
                            name = genre.name,
                        ),
                    )
                }

                // Insert Cast

                // Insert Seasons
                show.seasons.forEach { season ->
                    seasonDao.upsert(
                        Season(
                            id = Id(season.id.toLong()),
                            show_id = Id(show.id.toLong()),
                            season_number = season.seasonNumber.toLong(),
                            episode_count = season.episodeCount.toLong(),
                            title = season.name,
                            overview = season.overview,
                        ),
                    )
                }

                // Insert Videos
                show.videos.results.forEach { video ->
                    trailerDao.upsert(
                        Trailers(
                            id = video.id,
                            show_id = Id(show.id.toLong()),
                            key = video.key,
                            name = video.name,
                            site = video.site,
                            size = video.size.toLong(),
                            type = video.type,
                        ),
                    )
                }

                // Insert Network
                show.networks.forEach { network ->
                    networkDao.upsert(
                        Networks(
                            id = Id(network.id.toLong()),
                            name = network.name,
                            tmdb_id = Id(show.id.toLong()),
                            logo_path = formatterUtil.formatTmdbPosterPath(network.logoPath),
                        ),
                    )
                    networkDao.upsert(
                        Show_networks(
                            show_id = Id(show.id.toLong()),
                            network_id = Id(network.id.toLong()),
                        ),
                    )
                }

                // Update Last Request
                requestManagerRepository.insert(
                    LastRequest(
                        id = id,
                        entityId = id,
                        requestType = "TMDB_SHOW_DETAILS",
                    ),
                )
            }
        },
    ),
)
    .scope(scope.io)
    .build()
