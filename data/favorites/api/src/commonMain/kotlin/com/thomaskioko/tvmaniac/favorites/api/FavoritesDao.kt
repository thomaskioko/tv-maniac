package com.thomaskioko.tvmaniac.favorites.api

import kotlinx.coroutines.flow.Flow

public interface FavoritesDao {

    public fun observeFavoriteShows(): Flow<List<FavoriteShow>>

    public fun upsert(traktId: Long, rank: Long, listedAt: String)

    public fun deleteAll()
}
