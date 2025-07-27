package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.store.apiFetcher
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsDao
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerDao
import com.thomaskioko.tvmaniac.db.Cast_appearance
import com.thomaskioko.tvmaniac.db.Casts
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Season
import com.thomaskioko.tvmaniac.db.Trailers
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.db.TvshowDetails
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SHOW_DETAILS
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
class ShowDetailsStore(
    private val remoteDataSource: TmdbShowDetailsNetworkDataSource,
    private val castDao: CastDao,
    private val tvShowsDao: TvShowsDao,
    private val showDetailsDao: ShowDetailsDao,
    private val seasonDao: SeasonsDao,
    private val trailerDao: TrailerDao,
    private val formatterUtil: FormatterUtil,
    private val dateFormatter: PlatformDateFormatter,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, TvshowDetails> by storeBuilder(
    fetcher = apiFetcher { id ->
        remoteDataSource.getShowDetails(id)
    },
    sourceOfTruth = SourceOfTruth.of<Long, TmdbShowDetailsResponse, TvshowDetails>(
        reader = { id: Long -> showDetailsDao.observeTvShows(id) },
        writer = { id, show ->
            databaseTransactionRunner {
                tvShowsDao.upsert(
                    Tvshow(
                        id = Id(id),
                        name = show.name,
                        overview = show.overview,
                        language = show.originalLanguage,
                        status = show.status,
                        first_air_date = show.firstAirDate?.let { dateFormatter.getYear(it) },
                        popularity = show.popularity,
                        episode_numbers = show.numberOfEpisodes.toString(),
                        last_air_date = show.lastAirDate?.let { dateFormatter.getYear(it) },
                        season_numbers = show.numberOfSeasons.toString(),
                        vote_average = show.voteAverage,
                        vote_count = show.voteCount.toLong(),
                        genre_ids = show.genres.map { it.id },
                        poster_path = show.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                        backdrop_path = show.backdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                    ),
                )

                // Insert Cast
                show.credits.cast.forEach { cast ->
                    castDao.upsert(
                        Casts(
                            id = Id(cast.id.toLong()),
                            character_name = cast.character,
                            name = cast.name,
                            profile_path = cast.profilePath?.let { formatterUtil.formatTmdbPosterPath(it) },
                            popularity = cast.popularity,
                        ),
                    )
                    castDao.upsert(
                        Cast_appearance(
                            cast_id = Id(cast.id.toLong()),
                            show_id = Id(show.id.toLong()),
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
                            image_url = season.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
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
                requestManagerRepository.upsert(
                    entityId = id,
                    requestType = SHOW_DETAILS.name,
                )
            }
        },
    )
        .usingDispatchers(
            readDispatcher = dispatchers.databaseRead,
            writeDispatcher = dispatchers.databaseWrite,
        ),
).validator(
    Validator.by {
        withContext(dispatchers.io) {
            !requestManagerRepository.isRequestExpired(
                entityId = it.id.id,
                requestType = SHOW_DETAILS.name,
                threshold = SHOW_DETAILS.duration,
            )
        }
    },
).build()
