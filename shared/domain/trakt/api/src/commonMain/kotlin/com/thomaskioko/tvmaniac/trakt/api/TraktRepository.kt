package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
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

    fun observeFollowedShows(listId: Int, userSlug: String): Flow<Resource<Unit>>

    fun observeFollowedShows(): Flow<List<SelectFollowedShows>>

    fun observeFollowedShow(showId: Long): Flow<Boolean>

    fun observeUpdateFollowedShow(showId: Long, addToWatchList: Boolean) : Flow<Resource<Unit>>

    suspend fun updateFollowedShow(showId: Long, addToWatchList: Boolean)

    suspend fun syncFollowedShows()

}