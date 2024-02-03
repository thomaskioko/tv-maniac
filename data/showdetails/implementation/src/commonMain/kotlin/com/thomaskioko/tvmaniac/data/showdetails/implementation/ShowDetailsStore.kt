package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.core.db.Casts
import com.thomaskioko.tvmaniac.core.db.Genres
import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.core.db.Tvshows
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsDao
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerDao
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SHOW_DETAILS
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
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
    private val castDao: CastDao,
    private val tvShowsDao: TvShowsDao,
    private val showDetailsDao: ShowDetailsDao,
    private val seasonDao: SeasonsDao,
    private val trailerDao: TrailerDao,
    private val genreDao: DefaultGenreDao,
    private val formatterUtil: FormatterUtil,
    private val dateFormatter: PlatformDateFormatter,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val scope: AppCoroutineScope,
) : Store<Long, TvshowDetails> by StoreBuilder.from(
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
        reader = { id: Long -> showDetailsDao.observeTvShows(id) },
        writer = { id, show ->
            databaseTransactionRunner {
                tvShowsDao.upsert(
                    Tvshows(
                        id = Id(id),
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
                            tmdb_id = Id(id),
                            name = genre.name,
                        ),
                    )
                }

                // Insert Cast
                show.credits.cast.forEach { cast ->
                    castDao.upsert(
                        Casts(
                            id = Id(cast.id.toLong()),
                            tmdb_id = Id(show.id.toLong()),
                            character_name = cast.character,
                            name = cast.name,
                            profile_path = cast.profilePath?.let {
                                formatterUtil.formatTmdbPosterPath(it)
                            },
                            popularity = cast.popularity,
                            season_id = null,
                        ),
                    )
                }

                // Insert Seasons
                show.seasons.forEach { season ->
                    seasonDao.upsert(
                        Season(
                            id = Id(season.id.toLong()),
                            show_id = Id(id),
                            season_number = season.seasonNumber.toLong(),
                            episode_count = season.episodeCount.toLong(),
                            title = season.name,
                            overview = season.overview,
                            image_url = season.posterPath?.let {
                                formatterUtil.formatTmdbPosterPath(it)
                            },
                        ),
                    )
                }

                // Insert Videos
                show.videos.results.forEach { video ->
                    trailerDao.upsert(
                        Trailers(
                            id = video.id,
                            show_id = Id(id),
                            key = video.key,
                            name = video.name,
                            site = video.site,
                            size = video.size.toLong(),
                            type = video.type,
                        ),
                    )
                }

                // Update Last Request
                requestManagerRepository.insert(
                    entityId = id,
                    requestType = SHOW_DETAILS.name,
                )
            }
        },
    ),
)
    .scope(scope.io)
    .build()
