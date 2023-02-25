package com.thomaskioko.tvmaniac.trakt.implementation.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktFollowedCache
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

class TraktFollowedCacheImpl(
    private val database: TvManiacDatabase,
    private val coroutineContext: CoroutineContext
) : TraktFollowedCache {

    override fun insert(followedShow: Followed_shows) {
        database.transaction {
            database.followedShowsQueries.insertOrReplace(
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
        database.followedShowsQueries.selectFollowedShows()
            .executeAsList()

    override fun getUnsyncedFollowedShows(): List<Followed_shows> =
        database.followedShowsQueries.selectUnsyncedShows()
            .executeAsList()

    override fun observeFollowedShows(): Flow<List<SelectFollowedShows>> =
        database.followedShowsQueries.selectFollowedShows()
            .asFlow()
            .mapToList(coroutineContext)

    override fun updateShowSyncState(traktId: Long) {
        database.followedShowsQueries.updateFollowedState(
            id = traktId,
            synced = true
        )
    }

    override fun removeShow(traktId: Long) {
        database.followedShowsQueries.removeShow(traktId)
    }
}