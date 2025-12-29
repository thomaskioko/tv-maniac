package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.store.apiFetcher
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.db.Cast_appearance
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
import com.thomaskioko.tvmaniac.tmdb.api.TmdbSeasonDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbSeasonDetailsResponse
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class SeasonDetailsStore(
    private val remoteDataSource: TmdbSeasonDetailsNetworkDataSource,
    private val castDao: CastDao,
    private val episodesDao: EpisodesDao,
    private val seasonDetailsDao: SeasonDetailsDao,
    private val formatterUtil: FormatterUtil,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<SeasonDetailsParam, SeasonDetailsWithEpisodes> by storeBuilder(
    fetcher = apiFetcher { params: SeasonDetailsParam ->
        remoteDataSource.getSeasonDetails(params.showId, params.seasonNumber)
    },
    sourceOfTruth = SourceOfTruth.of<SeasonDetailsParam, TmdbSeasonDetailsResponse, SeasonDetailsWithEpisodes>(
        reader = { params: SeasonDetailsParam ->
            seasonDetailsDao.observeSeasonEpisodeDetails(
                params.showId,
                params.seasonNumber,
            )
        },
        writer = { params: SeasonDetailsParam, response ->
            databaseTransactionRunner {
                response.episodes.forEach { episode ->
                    episodesDao.insert(
                        Episode(
                            id = Id(episode.id.toLong()),
                            season_id = Id(params.seasonId),
                            show_id = Id(params.showId),
                            episode_number = episode.episodeNumber.toLong(),
                            title = episode.name,
                            overview = episode.overview,
                            runtime = episode.runtime?.toLong(),
                            vote_count = episode.voteCount.toLong(),
                            vote_average = episode.voteAverage,
                            image_url = episode.stillPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                            air_date = episode.airDate,
                            trakt_id = null,
                        ),
                    )
                }

                // Insert Season Images
                response.images.posters.forEach { image ->
                    seasonDetailsDao.upsertSeasonImage(
                        seasonId = params.seasonId,
                        imageUrl = formatterUtil.formatTmdbPosterPath(image.filePath),
                    )
                }

                // Insert Season Cast
                response.credits.cast.forEach { cast ->
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
                            show_id = Id(params.showId),
                            season_id = Id(params.seasonId),
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
