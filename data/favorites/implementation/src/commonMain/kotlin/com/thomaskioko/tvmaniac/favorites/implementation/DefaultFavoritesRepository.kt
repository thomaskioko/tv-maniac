package com.thomaskioko.tvmaniac.favorites.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.favorites.api.FavoriteShow
import com.thomaskioko.tvmaniac.favorites.api.FavoritesDao
import com.thomaskioko.tvmaniac.favorites.api.FavoritesRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultFavoritesRepository(
    private val dao: FavoritesDao,
    private val favoritesStore: FavoritesStore,
    private val accountManager: AccountManager,
) : FavoritesRepository {

    override fun observeFavorites(): Flow<List<FavoriteShow>> = dao.observeFavoriteShows()

    override suspend fun syncFavorites(forceRefresh: Boolean) {
        if (accountManager.getActiveProvider() != AccountProvider.TRAKT) return

        when {
            forceRefresh -> favoritesStore.fresh(Unit)
            else -> favoritesStore.get(Unit)
        }
    }
}
