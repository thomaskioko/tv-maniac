package com.thomaskioko.tvmaniac.trakt.api.cache

import com.thomaskioko.tvmaniac.core.db.Followed_shows
import kotlinx.coroutines.flow.Flow

interface TraktFollowedCache {

    fun insert(followedShows: Followed_shows)

    fun getFollowedShows(): List<Followed_shows>

    fun getUnsyncedFollowedShows(): List<Followed_shows>

    fun observeFollowedShows(): Flow<List<Followed_shows>>

    fun updateShowSyncState(showId: Long)

    fun removeShow(showId: Long)
}