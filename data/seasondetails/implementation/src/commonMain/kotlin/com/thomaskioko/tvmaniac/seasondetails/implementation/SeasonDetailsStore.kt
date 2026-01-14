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
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SEASON_DETAILS
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbSeasonDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
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
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<SeasonDetailsParam, SeasonDetailsWithEpisodes> by storeBuilder(
    fetcher = Fetcher.of { params: SeasonDetailsParam ->
        coroutineScope {
            // Get TMDB ID for TMDB API calls (supplementary data)
            val showTmdbId = tvShowsDao.getTmdbIdByTraktId(params.showTraktId)

            // Fetch Trakt episodes and TMDB data in parallel
            val traktSeasonDeferred = async {
                traktRemoteDataSource.getSeasonEpisodes(params.showTraktId, params.seasonNumber.toInt()).getOrThrow()
            }
            val tmdbSeasonDeferred = async {
                // TMDB data is supplementary - use getOrNull so failures don't break the fetch
                showTmdbId?.let {
                    tmdbRemoteDataSource.getSeasonDetails(it, params.seasonNumber).getOrNull()
                }
            }

            // Await and combine
            val traktSeason = traktSeasonDeferred.await()
            val tmdbSeason = tmdbSeasonDeferred.await()

            SeasonDetailsResponse(
                traktEpisodes = traktSeason,
                tmdbImages = tmdbSeason?.images,
                tmdbCredits = tmdbSeason?.credits,
                tmdbEpisodes = tmdbSeason?.episodes ?: emptyList(),
            )
        }
    },
    sourceOfTruth = SourceOfTruth.of<SeasonDetailsParam, SeasonDetailsResponse, SeasonDetailsWithEpisodes>(
        reader = { params: SeasonDetailsParam ->
            seasonDetailsDao.observeSeasonEpisodeDetails(
                params.showTraktId,
                params.seasonNumber,
            )
        },
        writer = { params: SeasonDetailsParam, response ->
            databaseTransactionRunner {
                // Create a map of TMDB episode images by episode number
                val tmdbEpisodeImages = response.tmdbEpisodes.associate {
                    it.episodeNumber to it.stillPath
                }

                // Insert Episodes (from Trakt with TMDB images)
                response.traktEpisodes.forEach { episode ->
                    val tmdbImage = tmdbEpisodeImages[episode.episodeNumber]
                    episodesDao.insert(
                        Episode(
                            id = Id(episode.ids.tmdb?.toLong() ?: episode.ids.trakt.toLong()),
                            season_id = Id(params.seasonId),
                            show_trakt_id = Id(params.showTraktId),
                            episode_number = episode.episodeNumber.toLong(),
                            title = episode.title,
                            overview = episode.overview ?: "",
                            runtime = episode.runtime?.toLong() ?: 0L,
                            vote_count = episode.votes?.toLong() ?: 0L,
                            ratings = episode.ratings ?: 0.0,
                            image_url = tmdbImage?.let { formatterUtil.formatTmdbPosterPath(it) },
                            air_date = episode.firstAired,
                            trakt_id = episode.ids.trakt.toLong(),
                        ),
                    )
                }

                // Insert Season Images (from TMDB) - optional
                response.tmdbImages?.posters?.let { posters ->
                    // Update the season's main image_url with the first poster
                    posters.firstOrNull()?.let { firstPoster ->
                        seasonsDao.updateImageUrl(
                            seasonId = params.seasonId,
                            imageUrl = formatterUtil.formatTmdbPosterPath(firstPoster.filePath),
                        )
                    }
                    // Also store all posters in season_images table for gallery
                    posters.forEach { image ->
                        seasonDetailsDao.upsertSeasonImage(
                            seasonId = params.seasonId,
                            imageUrl = formatterUtil.formatTmdbPosterPath(image.filePath),
                        )
                    }
                }

                // Insert Season Cast (from TMDB) - optional
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
    Validator.by {
        withContext(dispatchers.io) {
            !requestManagerRepository.isRequestExpired(
                entityId = it.seasonId,
                requestType = SEASON_DETAILS.name,
                threshold = SEASON_DETAILS.duration,
            )
        }
    },
)
    .build()
