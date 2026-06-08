package com.thomaskioko.tvmaniac.favorites.testing

import com.thomaskioko.tvmaniac.favorites.api.FavoriteShow
import com.thomaskioko.tvmaniac.favorites.api.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

public class FakeFavoritesRepository : FavoritesRepository {

    public data class SyncInvocation(val forceRefresh: Boolean)

    private val favorites = MutableStateFlow<List<FavoriteShow>>(emptyList())
    private val syncInvocations = mutableListOf<SyncInvocation>()
    private var observeError: Throwable? = null

    public fun setFavorites(shows: List<FavoriteShow>) {
        favorites.value = shows
    }

    public fun setObserveError(error: Throwable?) {
        observeError = error
    }

    public fun syncInvocations(): List<SyncInvocation> = syncInvocations.toList()

    public fun clearInvocations() {
        syncInvocations.clear()
    }

    override fun observeFavorites(): Flow<List<FavoriteShow>> =
        observeError?.let { error -> flow { throw error } } ?: favorites.asStateFlow()

    override suspend fun syncFavorites(forceRefresh: Boolean) {
        syncInvocations.add(SyncInvocation(forceRefresh = forceRefresh))
    }
}
