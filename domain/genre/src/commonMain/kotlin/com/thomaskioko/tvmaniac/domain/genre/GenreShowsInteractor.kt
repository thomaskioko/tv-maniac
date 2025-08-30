package com.thomaskioko.tvmaniac.domain.genre

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.genre.GenreRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
class GenreShowsInteractor(
    private val repository: GenreRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Boolean>() {
    override suspend fun doWork(params: Boolean) {
        withContext(dispatchers.io) {
            repository.fetchGenresWithShows(params)
        }
    }
}
