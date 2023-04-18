package com.thomaskioko.tvmaniac.trakt.profile.implementation.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.db.Trakt_shows_list
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.trakt.profile.api.cache.FavoriteListCache
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class FavoriteListCacheImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : FavoriteListCache {

    override fun insert(traktList: Trakt_shows_list) {
        database.trakt_shows_listQueries.insertOrReplace(
            id = traktList.id,
            slug = traktList.slug,
            description = traktList.description
        )
    }

    override fun getTraktList(): Trakt_shows_list? =
        database.trakt_shows_listQueries.selectShowsList().executeAsOneOrNull()

    override fun observeTraktList(): Flow<Trakt_shows_list> {
        return database.trakt_shows_listQueries.selectShowsList()
            .asFlow()
            .mapToOne(dispatchers.io)
    }
}