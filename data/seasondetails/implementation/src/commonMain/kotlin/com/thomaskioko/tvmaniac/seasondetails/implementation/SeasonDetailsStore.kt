package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.db.Casts
import com.thomaskioko.tvmaniac.core.db.Episode
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.resourcemanager.api.LastRequest
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import com.thomaskioko.tvmaniac.tmdb.api.TmdbSeasonDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.model.ApiResponse
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class SeasonDetailsStore(
    private val remoteDataSource: TmdbSeasonDetailsNetworkDataSource,
    private val castDao: CastDao,
    private val episodesDao: EpisodesDao,
    private val seasonDetailsDao: SeasonDetailsDao,
    private val scope: AppCoroutineScope,
    private val formatterUtil: FormatterUtil,
    private val requestManagerRepository: RequestManagerRepository,
) : Store<SeasonDetailsParam, SeasonDetailsWithEpisodes> by StoreBuilder.from(
    fetcher = Fetcher.of { params: SeasonDetailsParam ->
        when (
            val response = remoteDataSource.getSeasonDetails(params.showId, params.seasonNumber)
        ) {
            is ApiResponse.Success -> response.body
            is ApiResponse.Error.GenericError -> throw Throwable("${response.errorMessage}")
            is ApiResponse.Error.HttpError -> throw Throwable("${response.code} - ${response.errorMessage}")
            is ApiResponse.Error.SerializationError -> throw Throwable("${response.errorMessage}")
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { params: SeasonDetailsParam ->
            seasonDetailsDao.observeSeasonEpisodeDetails(params.showId, params.seasonNumber)
        },
        writer = { params: SeasonDetailsParam, response ->
            response.episodes.forEach { episode ->
                episodesDao.insert(
                    Episode(
                        id = Id(episode.id.toLong()),
                        show_id = Id(params.showId),
                        episode_number = episode.episodeNumber.toLong(),
                        title = episode.name,
                        overview = episode.overview,
                        image_url = episode.stillPath?.let {
                            formatterUtil.formatTmdbPosterPath(it)
                        },
                        vote_average = episode.voteAverage,
                        vote_count = episode.voteCount.toLong(),
                        runtime = episode.runtime?.toLong(),
                        season_id = Id(params.seasonId),
                    ),
                )
            }

            // Insert Season Videos

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
                        season_id = Id(params.seasonId),
                        character_name = cast.character,
                        name = cast.name,
                        profile_path = cast.profilePath?.let {
                            formatterUtil.formatTmdbPosterPath(it)
                        },
                        popularity = cast.popularity,
                        tmdb_id = null,
                    ),
                )
            }

            // Update Last Request
            requestManagerRepository.upsert(
                LastRequest(
                    id = params.showId,
                    entityId = params.seasonId,
                    requestType = "SEASON_DETAILS",
                ),
            )
        },
        delete = { params: SeasonDetailsParam -> seasonDetailsDao.delete(params.seasonId) },
        deleteAll = seasonDetailsDao::deleteAll,
    ),
)
    .scope(scope.io)
    .build()
