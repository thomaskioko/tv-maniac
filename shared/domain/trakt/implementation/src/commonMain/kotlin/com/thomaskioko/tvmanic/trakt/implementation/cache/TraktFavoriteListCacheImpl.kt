package com.thomaskioko.tvmanic.trakt.implementation.cache

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.db.Trakt_favorite_list
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktFavoriteListCache
import kotlinx.coroutines.flow.Flow

class TraktFavoriteListCacheImpl(
    private val database: TvManiacDatabase
) : TraktFavoriteListCache {

    override fun insert(favoriteList: Trakt_favorite_list) {
        database.traktFavoriteListQueries.insertOrReplace(
            id = favoriteList.id,
            slug = favoriteList.slug,
            description = favoriteList.description
        )
    }

    override fun getFavoriteList(): Trakt_favorite_list? =
        database.traktFavoriteListQueries.selectFavorite().executeAsOneOrNull()

    override fun observeFavoriteList(): Flow<Trakt_favorite_list?> {
        return database.traktFavoriteListQueries.selectFavorite()
            .asFlow()
            .mapToOneOrNull()
    }
}