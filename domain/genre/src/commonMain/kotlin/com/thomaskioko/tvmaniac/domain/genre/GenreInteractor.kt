package com.thomaskioko.tvmaniac.domain.genre

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.genre.GenreRepository
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class GenreInteractor(
    private val genreRepository: GenreRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Unit>() {
    override suspend fun doWork(params: Unit) {
        withContext(dispatchers.io) {
            genreRepository.observeGenrePosters()
        }
    }
}
