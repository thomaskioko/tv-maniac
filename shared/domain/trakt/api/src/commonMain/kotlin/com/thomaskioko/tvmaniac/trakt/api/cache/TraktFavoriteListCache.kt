package com.thomaskioko.tvmaniac.trakt.api.cache

import com.thomaskioko.tvmaniac.core.db.Trakt_favorite_list
import kotlinx.coroutines.flow.Flow

interface TraktFavoriteListCache {

    fun insert(favoriteList: Trakt_favorite_list)

    fun getFavoriteList(): Trakt_favorite_list?

    fun observeFavoriteList(): Flow<Trakt_favorite_list?>
}