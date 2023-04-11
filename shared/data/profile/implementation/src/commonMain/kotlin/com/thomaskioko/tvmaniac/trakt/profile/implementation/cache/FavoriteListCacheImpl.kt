package com.thomaskioko.tvmaniac.trakt.profile.implementation.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.FavoriteListCache
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class FavoriteListCacheImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : FavoriteListCache {

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
            .mapToOne(dispatchers.io)
    }
}