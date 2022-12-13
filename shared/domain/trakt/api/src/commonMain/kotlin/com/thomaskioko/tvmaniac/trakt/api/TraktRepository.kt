package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.TraktStats
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.network.Resource
import kotlinx.coroutines.flow.Flow

interface TraktRepository {
    fun observeMe(slug: String): Flow<Resource<Trakt_user>>

    fun observeStats(slug: String, refresh : Boolean = false): Flow<Resource<TraktStats>>

    fun observeCreateTraktList(userSlug: String): Flow<Resource<Trakt_list>>

    fun observeFollowedShows(): Flow<Resource<List<SelectFollowedShows>>>

    fun getFollowedShows(): List<SelectFollowedShows>

    fun observeUpdateFollowedShow(traktId: Int, addToWatchList: Boolean) : Flow<Resource<Unit>>

    fun observeShow(traktId: Int): Flow<Resource<SelectByShowId>>

    fun fetchShowsByCategoryId(categoryId: Int): Flow<Resource<List<SelectShowsByCategory>>>

    fun observeCachedShows(categoryId: Int):  Flow<Resource<List<SelectShowsByCategory>>>

    suspend fun updateFollowedShow(traktId: Int, addToWatchList: Boolean)

    suspend fun fetchTraktWatchlistShows()

    suspend fun fetchShows()

    suspend fun syncFollowedShows()

}