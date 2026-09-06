package com.thomaskioko.tvmaniac.domain.lists

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.lists.api.ListRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class FetchMissingListShowDetailsInteractor(
    private val listRepository: ListRepository,
    private val showDetailsRepository: ShowDetailsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<FetchMissingListShowDetailsInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {
            val tmdbIds = listRepository.getTmdbIdsMissingPoster(params.listId)
            for (tmdbId in tmdbIds) {
                showDetailsRepository.fetchShowDetails(id = tmdbId)
            }
        }
    }

    public data class Params(val listId: Long)
}
