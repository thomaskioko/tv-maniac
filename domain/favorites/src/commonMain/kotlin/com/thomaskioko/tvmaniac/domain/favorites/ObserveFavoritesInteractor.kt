package com.thomaskioko.tvmaniac.domain.favorites

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.favorites.api.FavoriteShow
import com.thomaskioko.tvmaniac.favorites.api.FavoritesRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObserveFavoritesInteractor(
    private val repository: FavoritesRepository,
) : SubjectInteractor<Unit, List<FavoriteShow>>() {

    override fun createObservable(params: Unit): Flow<List<FavoriteShow>> =
        repository.observeFavorites()
}
