package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.model.getOrThrow
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsDao
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Season
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.db.TvshowDetails
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SHOW_DETAILS
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
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
    private val formatterUtil: FormatterUtil,
    private val dateTimeProvider: DateTimeProvider,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, TvshowDetails> by storeBuilder(
    fetcher = Fetcher.of { traktId: Long ->
        coroutineScope {
            val showDetailsDeferred = async {
                traktRemoteDataSource.getShowDetails(traktId).getOrThrow()
            }
            val seasonsDeferred = async {
                traktRemoteDataSource.getShowSeasons(traktId).getOrThrow()
            }

            val showDetails = showDetailsDeferred.await()
            val seasons = seasonsDeferred.await()

            val tmdbId = showDetails.ids.tmdb
                ?: error("Show ${showDetails.title} (trakt: $traktId) has no TMDB ID")
            val tmdbDetails = tmdbRemoteDataSource.getShowDetails(tmdbId).getOrThrow()

            ShowDetailsResponse(
                traktShow = showDetails,
                traktSeasons = seasons,
                tmdbId = tmdbId,
                tmdbPosterPath = tmdbDetails.posterPath,
                tmdbBackdropPath = tmdbDetails.backdropPath,
            )
        }
    },
    sourceOfTruth = SourceOfTruth.of<Long, ShowDetailsResponse, TvshowDetails>(
        reader = { traktId: Long -> showDetailsDao.observeTvShowByTraktId(traktId) },
        writer = { traktId, response ->
            databaseTransactionRunner {
                val show = response.traktShow
                val tmdbId = response.tmdbId

                tvShowsDao.upsert(
                    Tvshow(
                        trakt_id = Id(traktId),
                        tmdb_id = Id(tmdbId),
                        name = show.title,
                        overview = show.overview ?: "",
                        language = show.language,
                        status = show.status,
                        year = show.firstAirDate?.let { dateTimeProvider.getYear(it) },
                        episode_numbers = show.airedEpisodes?.toString(),
                        season_numbers = response.traktSeasons.size.toString(),
                        ratings = show.rating ?: 0.0,
                        vote_count = show.votes ?: 0L,
                        genres = show.genres?.map { it.replaceFirstChar { char -> char.uppercase() } },
                        poster_path = response.tmdbPosterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                        backdrop_path = response.tmdbBackdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                    ),
                )

                response.traktSeasons.forEach { season ->
                    seasonDao.upsert(
                        Season(
                            id = Id(season.ids.tmdb?.toLong() ?: season.ids.trakt.toLong()),
                            show_trakt_id = Id(traktId),
                            season_number = season.number.toLong(),
                            episode_count = season.episodeCount.toLong(),
                            title = season.title,
                            overview = season.overview,
                            image_url = null,
                        ),
                    )
                }

                // Update Last Request
                requestManagerRepository.upsert(
                    entityId = traktId,
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
                entityId = it.trakt_id.id,
                requestType = SHOW_DETAILS.name,
                threshold = SHOW_DETAILS.duration,
            )
        }
    },
).build()
