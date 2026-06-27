package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.domain.showdetails.model.Providers
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@Inject
public class ObserveWatchProvidersInteractor(
    private val watchProviderRepository: WatchProviderRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : SubjectInteractor<Long, List<Providers>>() {

    override fun createObservable(params: Long): Flow<List<Providers>> =
        watchProviderRepository.observeWatchProviders(params)
            .map { it.toWatchProviderList() }
            .flowOn(dispatchers.io)
}
