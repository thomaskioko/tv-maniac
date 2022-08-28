package com.thomaskioko.tvmaniac.trakt.api.cache

import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShow
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import kotlinx.coroutines.flow.Flow

interface TraktFollowedCache {

    fun insert(followedShows: Followed_shows)

    fun getFollowedShows(): List<SelectFollowedShows>

    fun getUnsyncedFollowedShows(): List<Followed_shows>

    fun observeFollowedShows(): Flow<List<SelectFollowedShows>>

    fun observeFollowedShow(showId: Long): Flow<SelectFollowedShow?>

    fun updateShowSyncState(showId: Long)

    fun removeShow(showId: Long)
}