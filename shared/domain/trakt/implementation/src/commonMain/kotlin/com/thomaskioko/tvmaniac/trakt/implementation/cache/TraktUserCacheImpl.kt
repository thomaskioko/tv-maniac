package com.thomaskioko.tvmaniac.trakt.implementation.cache

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktUserCache
import kotlinx.coroutines.flow.Flow

class TraktUserCacheImpl(
    private val database: TvManiacDatabase
) : TraktUserCache {
    override fun insert(traktUser: Trakt_user) {
        database.traktUserQueries.insertOrReplace(
            slug = traktUser.slug,
            full_name = traktUser.full_name,
            profile_picture = traktUser.profile_picture,
            user_name = traktUser.user_name,
            is_me = traktUser.is_me
        )
    }

    override fun observeUserBySlug(slug: String): Flow<Trakt_user?> {
        return database.traktUserQueries.userBySlug(slug)
            .asFlow()
            .mapToOneOrNull()
    }

    override fun observeMe(): Flow<Trakt_user> {
        return database.traktUserQueries.getMe()
            .asFlow()
            .mapToOne()
    }

    override fun getMe(): Trakt_user? = database.traktUserQueries.getMe().executeAsOneOrNull()
}