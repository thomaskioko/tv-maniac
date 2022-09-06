package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.Trakt_favorite_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.network.Resource
import kotlinx.coroutines.flow.Flow

interface TraktRepository {
    fun observeMe(slug: String): Flow<Resource<Trakt_user>>

    fun observeCreateTraktFavoriteList(userSlug: String): Flow<Resource<Trakt_favorite_list>>

    fun observeFollowedShows(listId: Int, userSlug: String): Flow<Resource<Unit>>

    fun observeFollowedShows(): Flow<List<SelectFollowedShows>>

    fun observeFollowedShow(traktId: Int): Flow<Boolean>

    fun observeUpdateFollowedShow(traktId: Int, addToWatchList: Boolean) : Flow<Resource<Unit>>

    fun observeShow(traktId: Int): Flow<Resource<Show>>

    suspend fun updateFollowedShow(traktId: Int, addToWatchList: Boolean)

    suspend fun syncFollowedShows()

}