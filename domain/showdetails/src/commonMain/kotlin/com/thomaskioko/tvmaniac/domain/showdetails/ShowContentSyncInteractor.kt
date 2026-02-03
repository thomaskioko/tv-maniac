package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class ShowContentSyncInteractor(
    private val showDetailsRepository: ShowDetailsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<ShowContentSyncInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            try {
                showDetailsRepository.fetchShowDetails(
                    id = params.traktId,
                    forceRefresh = params.forceRefresh,
                )

                seasonDetailsRepository.syncShowSeasonDetails(
                    showTraktId = params.traktId,
                    forceRefresh = params.forceRefresh,
                )

                watchedEpisodeSyncRepository.syncShowEpisodeWatches(
                    showTraktId = params.traktId,
                    forceRefresh = params.forceRefresh,
                )
            } catch (t: Throwable) {
                logger.error("Error while updating show seasons/episodes: ${params.traktId}", t)
            }
        }
    }

    public data class Param(
        val traktId: Long,
        val forceRefresh: Boolean = false,
        val isUserInitiated: Boolean = false,
    )
}
