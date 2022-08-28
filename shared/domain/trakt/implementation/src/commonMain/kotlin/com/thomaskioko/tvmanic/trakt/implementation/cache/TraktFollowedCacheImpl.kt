package com.thomaskioko.tvmanic.trakt.implementation.cache

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

    override fun insert(followedShows: Followed_shows) {
        database.followedShowsQueries.insertOrReplace(
            show_id = followedShows.show_id,
            synced = followedShows.synced
        )
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

    override fun observeFollowedShow(showId: Long): Flow<SelectFollowedShow?> =
       database.followedShowsQueries.selectFollowedShow(showId)
           .asFlow()
           .mapToOneOrNull()

    override fun updateShowSyncState(showId: Long) {
        database.followedShowsQueries.updateFollowedState(
            show_id = showId,
            synced = true
        )
    }

    override fun removeShow(showId: Long) {
       database.followedShowsQueries.removeShow(showId)
    }
}