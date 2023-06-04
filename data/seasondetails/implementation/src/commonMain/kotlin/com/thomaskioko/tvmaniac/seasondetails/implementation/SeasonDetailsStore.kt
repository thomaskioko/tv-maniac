package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.db.Episodes
import com.thomaskioko.tvmaniac.core.db.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.Season_episodes
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktRemoteDataSource
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class SeasonDetailsStore(
    private val traktRemoteDataSource: TraktRemoteDataSource,
    private val seasonDetailsDao: SeasonDetailsDao,
    private val episodesDao: EpisodesDao,
    private val scope: AppCoroutineScope,
    private val logger: KermitLogger,
) : Store<Long, List<SeasonWithEpisodes>> by StoreBuilder
    .from<Long, List<SeasonWithEpisodes>, List<SeasonWithEpisodes>>(
        fetcher = Fetcher.of { id: Long ->
            when (val response = traktRemoteDataSource.getSeasonEpisodes(id)) {
                is ApiResponse.Success -> response.body.toSeasonWithEpisodes()
                is ApiResponse.Error.GenericError -> {
                    logger.error("GenericError", "$response")
                    throw Throwable("${response.errorMessage}")
                }

                is ApiResponse.Error.HttpError -> {
                    logger.error("HttpError", "$response")
                    throw Throwable("${response.code} - ${response.errorBody?.message}")
                }

                is ApiResponse.Error.SerializationError -> {
                    logger.error("SerializationError", "$response")
                    throw Throwable("$response")
                }
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = seasonDetailsDao::observeShowEpisodes,
            writer = { id, list ->
                list.forEach { season ->
                    episodesDao.insert(
                        Episodes(
                            trakt_id = season.trakt_id,
                            season_id = season.season_id,
                            title = season.title,
                            tmdb_id = season.tmdb_id,
                            overview = season.overview,
                            ratings = season.ratings,
                            runtime = season.runtime,
                            votes = season.votes,
                            episode_number = season.episode_number,
                        ),
                    )

                    seasonDetailsDao.insert(
                        Season_episodes(
                            show_id = id,
                            season_id = season.trakt_id,
                            season_number = season.season_number,
                        ),
                    )
                }
            },
            delete = seasonDetailsDao::delete,
            deleteAll = seasonDetailsDao::deleteAll,
        ),
    )
    .scope(scope.io)
    .build()
