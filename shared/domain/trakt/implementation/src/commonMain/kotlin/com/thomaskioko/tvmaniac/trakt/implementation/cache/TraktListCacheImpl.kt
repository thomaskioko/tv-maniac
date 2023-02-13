package com.thomaskioko.tvmaniac.trakt.implementation.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktListCache
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

class TraktListCacheImpl(
    private val database: TvManiacDatabase,
    private val coroutineContext: CoroutineContext
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

    override fun observeTraktList(): Flow<Trakt_list> {
        return database.traktListQueries.selectFavorite()
            .asFlow()
            .mapToOne(coroutineContext)
    }
}