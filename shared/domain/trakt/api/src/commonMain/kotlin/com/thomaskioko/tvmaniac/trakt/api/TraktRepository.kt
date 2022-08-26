package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.db.Trakt_favorite_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.network.Resource
import kotlinx.coroutines.flow.Flow

interface TraktRepository {
    fun observeMe(slug: String): Flow<Resource<Trakt_user>>

    fun observeCreateTraktFavoriteList(userSlug: String): Flow<Resource<Trakt_favorite_list>>

    fun observeAddShowToTraktFavoriteList(
        userSlug: String,
        listId: Long,
        tmdbShowId: Long
    ): Flow<Resource<Unit>>

    fun observeRemoveShowFromTraktFavoriteList(
        userSlug: String,
        listId: Long,
        tmdbShowId: Long
    ): Flow<Resource<Unit>>

    fun getLocalTraktUser(): Trakt_user?

    fun getFavoriteList(): Trakt_favorite_list?
}