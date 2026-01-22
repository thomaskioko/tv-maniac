package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.model.getOrNull
import com.thomaskioko.tvmaniac.core.networkutil.model.getOrThrow
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.db.Casts
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Episode
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonDetails
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SEASON_DETAILS
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbSeasonDetailsNetworkDataSource
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
public class SeasonDetailsStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tmdbRemoteDataSource: TmdbSeasonDetailsNetworkDataSource,
    private val tvShowsDao: TvShowsDao,
    private val castDao: CastDao,
    private val episodesDao: EpisodesDao,
    private val seasonsDao: SeasonsDao,
    private val seasonDetailsDao: SeasonDetailsDao,
    private val formatterUtil: FormatterUtil,
    private val dateTimeProvider: DateTimeProvider,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<SeasonDetailsParam, List<SeasonDetails>> by storeBuilder(
    fetcher = Fetcher.of { params: SeasonDetailsParam ->
        coroutineScope {
            val showTmdbId = tvShowsDao.getTmdbIdByTraktId(params.showTraktId)

            val traktSeason = async {
                traktRemoteDataSource.getSeasonEpisodes(params.showTraktId, params.seasonNumber.toInt()).getOrThrow()
            }.await()
            val tmdbSeason = async {
                showTmdbId?.let {
                    tmdbRemoteDataSource.getSeasonDetails(it, params.seasonNumber).getOrNull()
                }
            }.await()

            SeasonDetailsResponse(
                traktEpisodes = traktSeason,
                tmdbImages = tmdbSeason?.images,
                tmdbCredits = tmdbSeason?.credits,
                tmdbEpisodes = tmdbSeason?.episodes ?: emptyList(),
            )
        }
    },
    sourceOfTruth = SourceOfTruth.of<SeasonDetailsParam, SeasonDetailsResponse, List<SeasonDetails>>(
        reader = { params: SeasonDetailsParam ->
            seasonDetailsDao.observeSeasonDetails(
                params.showTraktId,
                params.seasonNumber,
            )
        },
        writer = { params: SeasonDetailsParam, response ->
            databaseTransactionRunner {
                val tmdbEpisodeImages = response.tmdbEpisodes.associate {
                    it.episodeNumber to it.stillPath
                }

                response.traktEpisodes.forEach { episode ->
                    val tmdbImage = tmdbEpisodeImages[episode.episodeNumber]
                    episodesDao.insert(
                        Episode(
                            id = Id(episode.ids.trakt.toLong()),
                            season_id = Id(params.seasonId),
                            show_trakt_id = Id(params.showTraktId),
                            episode_number = episode.episodeNumber.toLong(),
                            title = episode.title,
                            overview = episode.overview ?: "",
                            runtime = episode.runtime?.toLong() ?: 0L,
                            vote_count = episode.votes?.toLong() ?: 0L,
                            ratings = episode.ratings ?: 0.0,
                            image_url = tmdbImage?.let { formatterUtil.formatTmdbPosterPath(it) },
                            trakt_id = episode.ids.trakt.toLong(),
                            first_aired = dateTimeProvider.isoDateToEpoch(episode.firstAired),
                        ),
                    )
                }

                response.tmdbImages?.posters?.let { posters ->
                    posters.firstOrNull()?.let { firstPoster ->
                        seasonsDao.updateImageUrl(
                            seasonId = params.seasonId,
                            imageUrl = formatterUtil.formatTmdbPosterPath(firstPoster.filePath),
                        )
                    }
                    posters.forEach { image ->
                        seasonDetailsDao.upsertSeasonImage(
                            seasonId = params.seasonId,
                            imageUrl = formatterUtil.formatTmdbPosterPath(image.filePath),
                        )
                    }
                }

                // TODO:: Migrate to Fetch from Trakt
                response.tmdbCredits?.cast?.forEach { cast ->
                    castDao.upsert(
                        Casts(
                            id = Id(cast.id.toLong()),
                            trakt_id = null, // TMDB doesn't provide Trakt ID
                            show_trakt_id = Id(params.showTraktId),
                            season_id = Id(params.seasonId),
                            name = cast.name,
                            character_name = cast.character,
                            profile_path = cast.profilePath?.let { formatterUtil.formatTmdbPosterPath(it) },
                            popularity = cast.popularity,
                        ),
                    )
                }

                // Update Last Request
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
