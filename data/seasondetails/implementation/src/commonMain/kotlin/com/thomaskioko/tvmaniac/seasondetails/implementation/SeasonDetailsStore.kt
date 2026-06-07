package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.db.Casts
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonDetails
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SEASON_DETAILS
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbSeasonDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbSeasonDetailsResponse
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class SeasonDetailsStore(
    private val tmdbRemoteDataSource: TmdbSeasonDetailsNetworkDataSource,
    private val tvShowsDao: TvShowsDao,
    private val castDao: CastDao,
    private val episodesDao: EpisodesDao,
    private val seasonsDao: SeasonsDao,
    private val seasonDetailsDao: SeasonDetailsDao,
    private val showIdResolver: ShowIdResolver,
    private val tmdbSeasonMapper: TmdbSeasonMapper,
    private val formatterUtil: FormatterUtil,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<SeasonDetailsParam, List<SeasonDetails>> by storeBuilder(
    fetcher = Fetcher.of { params: SeasonDetailsParam ->
        val showTmdbId = tvShowsDao.getTmdbIdByShowId(params.showId)
            ?: error("No tmdb id for show ${params.showId}")
        tmdbRemoteDataSource.getSeasonDetails(showTmdbId, params.seasonNumber).getOrThrow()
    },
    sourceOfTruth = SourceOfTruth.of<SeasonDetailsParam, TmdbSeasonDetailsResponse, List<SeasonDetails>>(
        reader = { params: SeasonDetailsParam ->
            seasonDetailsDao.observeSeasonDetails(
                params.showId,
                params.seasonNumber,
            )
        },
        writer = { params: SeasonDetailsParam, response ->
            databaseTransactionRunner {
                val showId = showIdResolver.showIdForTraktId(params.showId)
                    ?: return@databaseTransactionRunner

                episodesDao.insert(tmdbSeasonMapper.mapToEpisodes(response, showId))

                response.images.posters.firstOrNull()?.let { firstPoster ->
                    seasonsDao.updateImageUrl(
                        seasonId = params.seasonId,
                        imageUrl = formatterUtil.formatTmdbPosterPath(firstPoster.filePath),
                    )
                }
                response.images.posters.forEach { image ->
                    seasonDetailsDao.upsertSeasonImage(
                        seasonId = params.seasonId,
                        imageUrl = formatterUtil.formatTmdbPosterPath(image.filePath),
                    )
                }

                response.credits.cast.forEach { cast ->
                    castDao.upsert(
                        Casts(
                            id = Id(cast.id.toLong()),
                            trakt_id = null,
                            show_id = showId,
                            season_id = Id(params.seasonId),
                            name = cast.name,
                            character_name = cast.character,
                            profile_path = cast.profilePath?.let { formatterUtil.formatTmdbPosterPath(it) },
                            popularity = cast.popularity,
                        ),
                    )
                }

                requestManagerRepository.upsert(
                    entityId = params.seasonId,
                    requestType = SEASON_DETAILS.name,
                )
            }
        },
        delete = { params: SeasonDetailsParam ->
            databaseTransactionRunner {
                seasonDetailsDao.delete(params.seasonId)
            }
        },
        deleteAll = { databaseTransactionRunner(seasonDetailsDao::deleteAll) },
    )
        .usingDispatchers(
            readDispatcher = dispatchers.databaseRead,
            writeDispatcher = dispatchers.databaseWrite,
        ),
).validator(
    Validator.by { seasonDetailsList ->
        seasonDetailsList.firstOrNull()?.season_id?.id?.let { seasonId ->
            withContext(dispatchers.io) {
                !requestManagerRepository.isRequestExpired(
                    entityId = seasonId,
                    requestType = SEASON_DETAILS.name,
                    threshold = SEASON_DETAILS.duration,
                )
            }
        } ?: false
    },
)
    .build()
