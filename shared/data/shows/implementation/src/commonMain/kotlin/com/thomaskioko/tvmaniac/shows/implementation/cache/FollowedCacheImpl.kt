package com.thomaskioko.tvmaniac.shows.implementation.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.cache.FollowedCache
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class FollowedCacheImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : FollowedCache {

    override fun insert(followedShow: Followed_shows) {
        database.transaction {
            database.followed_showsQueries.insertOrReplace(
                id = followedShow.id,
                synced = followedShow.synced,
                created_at = followedShow.created_at
            )
        }
    }

    override fun insert(followedShows: List<Followed_shows>) {
        followedShows.forEach { insert(it) }
    }

    override fun getFollowedShows(): List<SelectFollowedShows> =
        database.followed_showsQueries.selectFollowedShows()
            .executeAsList()

    override fun getUnsyncedFollowedShows(): List<Followed_shows> =
        database.followed_showsQueries.selectUnsyncedShows()
            .executeAsList()

    override fun observeFollowedShows(): Flow<List<SelectFollowedShows>> =
        database.followed_showsQueries.selectFollowedShows()
            .asFlow()
            .mapToList(dispatchers.io)

    override fun updateShowSyncState(traktId: Long) {
        database.followed_showsQueries.updateFollowedState(
            id = traktId,
            synced = true
        )
    }

    override fun removeShow(traktId: Long) {
        database.followed_showsQueries.removeShow(traktId)
    }
}