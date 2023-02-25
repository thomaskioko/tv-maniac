package com.thomaskioko.tvmaniac.trakt.implementation.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktUserCache
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

class TraktUserCacheImpl(
    private val database: TvManiacDatabase,
    private val coroutineContext: CoroutineContext
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
            .mapToOneOrNull(coroutineContext)
    }

    override fun observeMe(): Flow<Trakt_user> {
        return database.traktUserQueries.getMe()
            .asFlow()
            .mapToOne(coroutineContext)
    }

    override fun getMe(): Trakt_user? = database.traktUserQueries.getMe().executeAsOneOrNull()
}