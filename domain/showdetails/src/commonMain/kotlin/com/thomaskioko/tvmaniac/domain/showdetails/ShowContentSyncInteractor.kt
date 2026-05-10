package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class ShowContentSyncInteractor(
    private val showDetailsRepository: ShowDetailsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val syncObserver: SyncObserver,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<ShowContentSyncInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        syncObserver.trackSync("ShowContentSync-${params.traktId}") {
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
    }

    public data class Param(
        val traktId: Long,
        val forceRefresh: Boolean = false,
        val isUserInitiated: Boolean = false,
    )
}
