package com.thomaskioko.tvmaniac.domain.favorites

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.favorites.api.FavoritesRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class SyncFavoritesInteractor(
    private val favoritesRepository: FavoritesRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<SyncFavoritesInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            favoritesRepository.syncFavorites(params.forceRefresh)
        }
    }

    public data class Param(
        val forceRefresh: Boolean = false,
    )
}
