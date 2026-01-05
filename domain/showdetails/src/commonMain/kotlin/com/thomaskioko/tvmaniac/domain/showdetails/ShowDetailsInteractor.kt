package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor.Param
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class ShowDetailsInteractor(
    private val showDetailsRepository: ShowDetailsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            showDetailsRepository.fetchShowDetails(id = params.id, forceRefresh = params.forceRefresh)
        }
    }

    public data class Param(val id: Long, val forceRefresh: Boolean = false)
}
