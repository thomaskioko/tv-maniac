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
            ?: error("No trakt id for tmdb show $tmdbShowId")
        val showDetails = traktRemoteDataSource.getShowDetails(traktId).getOrThrow()

        val tmdbId = showDetails.ids.tmdb
            ?: error("Show ${showDetails.title} (trakt: $traktId) has no TMDB ID")
        val tmdbDetails = tmdbRemoteDataSource.getShowDetails(tmdbId).getOrThrow()

        requestManagerRepository.upsert(
            entityId = tmdbShowId,
            requestType = SHOW_DETAILS.name,
        )

        ShowDetailsResponse(
            traktShow = showDetails,
            tmdbSeasons = tmdbDetails.seasons,
            tmdbId = tmdbId,
            tmdbPosterPath = tmdbDetails.posterPath,
            tmdbBackdropPath = tmdbDetails.backdropPath,
        )
    },
    sourceOfTruth = SourceOfTruth.of<Long, ShowDetailsResponse, TvshowDetails>(
        reader = { tmdbShowId: Long -> showDetailsDao.observeTvShowByShowId(tmdbShowId) },
        writer = { tmdbShowId, response ->
            databaseTransactionRunner {
                val show = response.traktShow
                val traktId = show.ids.trakt
                val tmdbId = response.tmdbId

                tvShowsDao.upsert(
                    ShowToPersist(
                        showId = Id(traktId),
                        tmdbId = Id(tmdbId),
                        name = show.title,
                        overview = show.overview ?: "",
                        language = show.language,
                        status = show.status,
                        year = show.firstAirDate?.let { dateTimeProvider.extractYear(it) },
                        episodeNumbers = show.airedEpisodes?.toString(),
                        seasonNumbers = response.tmdbSeasons.size.toString(),
                        ratings = show.rating ?: 0.0,
                        voteCount = show.votes ?: 0L,
                        genres = show.genres?.map { it.replaceFirstChar { char -> char.uppercase() } },
                        posterPath = response.tmdbPosterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                        backdropPath = response.tmdbBackdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
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
