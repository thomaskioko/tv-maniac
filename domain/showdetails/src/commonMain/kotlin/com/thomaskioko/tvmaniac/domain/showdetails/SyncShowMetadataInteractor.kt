package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class SyncShowMetadataInteractor(
    private val showDetailsRepository: ShowDetailsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val watchProviderRepository: WatchProviderRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<SyncShowMetadataInteractor.Param>() {

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
            watchProviderRepository.fetchWatchProviders(
                traktId = params.traktId,
                forceRefresh = params.forceRefresh,
            )
        }
    }

    public data class Param(
        val traktId: Long,
        val forceRefresh: Boolean = false,
    )
}
