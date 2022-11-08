package com.thomaskioko.tvmaniac.trakt.implementation.cache

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShow
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktFollowedCache
import kotlinx.coroutines.flow.Flow

class TraktFollowedCacheImpl(
    private val database: TvManiacDatabase
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
            .mapToList()

    override fun observeFollowedShow(traktId: Int): Flow<SelectFollowedShow?> =
        database.followedShowsQueries.selectFollowedShow(traktId)
            .asFlow()
            .mapToOneOrNull()

    override fun updateShowSyncState(traktId: Int) {
        database.followedShowsQueries.updateFollowedState(
            id = traktId,
            synced = true
        )
    }

    override fun removeShow(traktId: Int) {
        database.followedShowsQueries.removeShow(traktId)
    }
}