package com.thomaskioko.tvmanic.trakt.implementation.cache

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktListCache
import kotlinx.coroutines.flow.Flow

class TraktListCacheImpl(
    private val database: TvManiacDatabase
) : TraktListCache {

    override fun insert(traktList: Trakt_list) {
        database.traktListQueries.insertOrReplace(
            id = traktList.id,
            slug = traktList.slug,
            description = traktList.description
        )
    }

    override fun getTraktList(): Trakt_list? =
        database.traktListQueries.selectFavorite().executeAsOneOrNull()

    override fun observeTraktList(): Flow<Trakt_list?> {
        return database.traktListQueries.selectFavorite()
            .asFlow()
            .mapToOneOrNull()
    }
}