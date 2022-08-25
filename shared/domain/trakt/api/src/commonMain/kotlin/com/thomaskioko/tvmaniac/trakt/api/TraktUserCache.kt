package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.db.Trakt_user
import kotlinx.coroutines.flow.Flow

interface TraktUserCache {

    fun insert(traktUser: Trakt_user)

    fun getUserBySlug(slug: String) : Flow<Trakt_user?>

    fun getMe() : Flow<Trakt_user?>
}