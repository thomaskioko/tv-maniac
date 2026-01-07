package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class ShowContentSyncInteractor(
    private val showDetailsRepository: ShowDetailsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<ShowContentSyncInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            logger.debug(TAG, "Syncing content for show ${params.showId}...")

            showDetailsRepository.fetchShowDetails(
                id = params.showId,
                forceRefresh = params.forceRefresh,
            )

            fetchAllSeasonDetails(params.showId, params.forceRefresh)

            watchedEpisodeSyncRepository.syncShowEpisodeWatches(
                showId = params.showId,
                forceRefresh = params.forceRefresh,
            )

            logger.debug(TAG, "Content sync complete for show ${params.showId}")
        }
    }

    private suspend fun fetchAllSeasonDetails(showId: Long, forceRefresh: Boolean) {
        val seasons = seasonsRepository.observeSeasonsByShowId(showId).first()
        logger.debug(TAG, "Fetching ${seasons.size} seasons for show $showId")

        seasons.forEach { season ->
            seasonDetailsRepository.fetchSeasonDetails(
                param = SeasonDetailsParam(
                    showId = showId,
                    seasonId = season.season_id.id,
                    seasonNumber = season.season_number,
                ),
                forceRefresh = forceRefresh,
            )
        }
    }

    public data class Param(
        val showId: Long,
        val forceRefresh: Boolean = false,
        val isUserInitiated: Boolean = false,
    )

    private companion object {
        private const val TAG = "ShowContentSyncInteractor"
    }
}
