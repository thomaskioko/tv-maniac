package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class SyncWatchedShowInteractor(
    private val showDetailsRepository: ShowDetailsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<SyncWatchedShowInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
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
        }
    }

    public data class Param(
        val traktId: Long,
        val forceRefresh: Boolean = false,
    )

    private companion object {
        private const val TAG = "SyncWatchedShowInteractor"
    }
}
