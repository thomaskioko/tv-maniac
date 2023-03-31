package com.thomaskioko.tvmaniac.trakt.profile.api.cache

import com.thomaskioko.tvmaniac.core.db.Trakt_user
import kotlinx.coroutines.flow.Flow

interface TraktUserCache {

    fun insert(traktUser: Trakt_user)

    fun observeUserBySlug(slug: String) : Flow<Trakt_user?>

    fun observeMe(): Flow<Trakt_user>

    fun getMe() : Trakt_user?
}