package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor.Param
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class ShowDetailsInteractor(
    private val showDetailsRepository: ShowDetailsRepository,
    private val castRepository: CastRepository,
    private val trailerRepository: TrailerRepository,
    private val providerRepository: WatchProviderRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            showDetailsRepository.fetchShowDetails(id = params.id, forceRefresh = params.forceRefresh)
            castRepository.fetchShowCast(showTraktId = params.id, forceRefresh = params.forceRefresh)
            trailerRepository.fetchTrailers(traktId = params.id, forceRefresh = params.forceRefresh)
            providerRepository.fetchWatchProviders(traktId = params.id, forceRefresh = params.forceRefresh)
        }
    }

    public data class Param(val id: Long, val forceRefresh: Boolean = false)
}
