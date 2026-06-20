package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsDao
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Season
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.TvshowDetails
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SHOW_DETAILS
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class ShowDetailsStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tmdbRemoteDataSource: TmdbShowDetailsNetworkDataSource,
    private val tvShowsDao: TvShowsDao,
    private val showDetailsDao: ShowDetailsDao,
    private val seasonDao: SeasonsDao,
    private val showIdResolver: ShowIdResolver,
    private val formatterUtil: FormatterUtil,
    private val dateTimeProvider: DateTimeProvider,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, TvshowDetails> by storeBuilder(
    fetcher = Fetcher.of { tmdbShowId: Long ->
        val traktId = tvShowsDao.getTraktIdByTmdbId(tmdbShowId)

        val response = if (traktId != null) {
            val traktShow = traktRemoteDataSource.getShowDetails(traktId).getOrThrow()
            val tmdbId = traktShow.ids.tmdb
                ?: error("Show ${traktShow.title} (trakt: $traktId) has no TMDB ID")
            val tmdbDetails = tmdbRemoteDataSource.getShowDetails(tmdbId).getOrThrow()

            ShowDetailsResponse(
                name = traktShow.title,
                overview = traktShow.overview,
                language = traktShow.language,
                status = traktShow.status,
                year = traktShow.firstAirDate?.let { dateTimeProvider.extractYear(it) },
                episodeNumbers = traktShow.airedEpisodes?.toString(),
                seasonNumbers = tmdbDetails.seasons.size.toString(),
                ratings = traktShow.rating ?: 0.0,
                voteCount = traktShow.votes ?: 0L,
                genres = traktShow.genres?.map { it.replaceFirstChar { c -> c.uppercase() } },
                posterPath = tmdbDetails.posterPath,
                backdropPath = tmdbDetails.backdropPath,
                tmdbSeasons = tmdbDetails.seasons,
                tmdbId = tmdbId,
                traktId = traktShow.ids.trakt,
            )
        } else {
            val tmdbShow = tmdbRemoteDataSource.getShowDetails(tmdbShowId).getOrThrow()

            ShowDetailsResponse(
                name = tmdbShow.name,
                overview = tmdbShow.overview,
                language = tmdbShow.originalLanguage,
                status = tmdbShow.status,
                year = tmdbShow.firstAirDate?.let { dateTimeProvider.extractYear(it) },
                episodeNumbers = tmdbShow.numberOfEpisodes.toString(),
                seasonNumbers = tmdbShow.numberOfSeasons.toString(),
                ratings = tmdbShow.voteAverage,
                voteCount = tmdbShow.voteCount.toLong(),
                genres = tmdbShow.genres.map { it.name.replaceFirstChar { c -> c.uppercase() } },
                posterPath = tmdbShow.posterPath,
                backdropPath = tmdbShow.backdropPath,
                tmdbSeasons = tmdbShow.seasons,
                tmdbId = tmdbShowId,
                traktId = null,
            )
        }

        requestManagerRepository.upsert(
            entityId = tmdbShowId,
            requestType = SHOW_DETAILS.name,
        )

        response
    },
    sourceOfTruth = SourceOfTruth.of<Long, ShowDetailsResponse, TvshowDetails>(
        reader = { tmdbShowId: Long -> showDetailsDao.observeTvShowByShowId(tmdbShowId) },
        writer = { tmdbShowId, response ->
            databaseTransactionRunner {
                tvShowsDao.upsert(
                    ShowToPersist(
                        showId = response.traktId?.let { Id(it) },
                        tmdbId = Id(response.tmdbId),
                        name = response.name,
                        overview = response.overview ?: "",
                        language = response.language,
                        status = response.status,
                        year = response.year,
                        episodeNumbers = response.episodeNumbers,
                        seasonNumbers = response.seasonNumbers,
                        ratings = response.ratings,
                        voteCount = response.voteCount,
                        genres = response.genres,
                        posterPath = response.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                        backdropPath = response.backdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                    ),
                )

                val internalShowId = showIdResolver.showIdForTmdbId(tmdbShowId)
                    ?: return@databaseTransactionRunner

                response.tmdbSeasons.forEach { season ->
                    seasonDao.upsert(
                        Season(
                            id = Id(season.id.toLong()),
                            show_id = internalShowId,
                            season_number = season.seasonNumber.toLong(),
                            episode_count = season.episodeCount.toLong(),
                            title = season.name,
                            overview = season.overview ?: "",
                            image_url = null,
                        ),
                    )
                }
            }
        },
    )
        .usingDispatchers(
            readDispatcher = dispatchers.databaseRead,
            writeDispatcher = dispatchers.databaseWrite,
        ),
).validator(
    Validator.by { show ->
        withContext(dispatchers.io) {
            !requestManagerRepository.isRequestExpired(
                entityId = show.tmdb_id.id,
                requestType = SHOW_DETAILS.name,
                threshold = SHOW_DETAILS.duration,
            )
        }
    },
).build()
