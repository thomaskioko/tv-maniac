package com.thomaskioko.tvmaniac.trakt.api.cache

import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import kotlinx.coroutines.flow.Flow

interface TraktFollowedCache {

    fun insert(followedShow: Followed_shows)

    fun insert(followedShows: List<Followed_shows>)

    fun getFollowedShows(): List<SelectFollowedShows>

    fun getUnsyncedFollowedShows(): List<Followed_shows>

    fun observeFollowedShows(): Flow<List<SelectFollowedShows>>

    fun updateShowSyncState(traktId: Long)

    fun removeShow(traktId: Long)
}