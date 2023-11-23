package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.SeasonEpisodeDetailsById
import com.thomaskioko.tvmaniac.db.DbTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.ApiResponse
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class SeasonDetailsStore(
    private val remoteDataSource: TraktShowsRemoteDataSource,
    private val seasonCache: SeasonsDao,
    private val episodesDao: EpisodesDao,
    private val scope: AppCoroutineScope,
    private val dbTransactionRunner: DbTransactionRunner,
    private val logger: KermitLogger,
) : Store<Long, List<SeasonEpisodeDetailsById>> by StoreBuilder
    .from(
        fetcher = Fetcher.of { id: Long ->
            when (val response = remoteDataSource.getSeasonEpisodes(id)) {
                is ApiResponse.Success -> response.body.toSeasonWithEpisodes()
                is ApiResponse.Error.GenericError -> {
                    logger.error("SeasonDetailsStore GenericError", "$response")
                    throw Throwable("${response.errorMessage}")
                }

                is ApiResponse.Error.HttpError -> {
                    logger.error("SeasonDetailsStore HttpError", "$response")
                    throw Throwable("${response.code} - ${response.errorMessage}")
                }

                is ApiResponse.Error.SerializationError -> {
                    logger.error("SeasonDetailsStore SerializationError", "$response")
                    throw Throwable("${response.errorMessage}")
                }
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = seasonCache::observeSeasonEpisodeDetailsById,
            writer = { id, list ->
                dbTransactionRunner {
                    list.forEach { season ->
                        seasonCache.upsert(
                            Season(
                                id = Id(season.seasonId),
                                show_id = Id(id),
                                season_number = season.seasonNumber,
                                title = season.title,
                                episode_count = season.episodeCount,
                                overview = season.overview,
                            ),
                        )

                        episodesDao.insert(season.episodes)
                    }
                }
            },
            delete = seasonCache::delete,
            deleteAll = seasonCache::deleteAll,
        ),
    )
    .scope(scope.io)
    .build()
