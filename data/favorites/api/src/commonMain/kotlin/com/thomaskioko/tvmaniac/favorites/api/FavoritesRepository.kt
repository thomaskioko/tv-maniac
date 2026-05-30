package com.thomaskioko.tvmaniac.favorites.api

import kotlinx.coroutines.flow.Flow

public interface FavoritesRepository {

    /**
     * Observes the user's Trakt favorited shows, ordered by favorite rank.
     * */
    public fun observeFavorites(): Flow<List<FavoriteShow>>

    /**
     * Re-syncs the Trakt favorites list, writing show metadata and posters into the shared show
     * tables. Pull-only: never uploads local changes.
     *
     * @param forceRefresh bypasses the cache freshness window when true.
     */
    public suspend fun syncFavorites(forceRefresh: Boolean = false)
}
