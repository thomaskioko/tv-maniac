package com.thomaskioko.tvmaniac.trakt.api

import com.kuuurt.paging.multiplatform.PagingData
import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.Trakt_favorite_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.CommonFlow
import com.thomaskioko.tvmaniac.core.util.network.Resource
import kotlinx.coroutines.flow.Flow

interface TraktRepository {
    fun observeMe(slug: String): Flow<Resource<Trakt_user>>

    fun observeCreateTraktFavoriteList(userSlug: String): Flow<Resource<Trakt_favorite_list>>

    fun observeFollowedShows(): Flow<List<SelectFollowedShows>>

    fun observeFollowedShow(traktId: Int): Flow<Boolean>

    fun observeUpdateFollowedShow(traktId: Int, addToWatchList: Boolean) : Flow<Resource<Unit>>

    fun observeShow(traktId: Int): Flow<Resource<SelectByShowId>>

    fun observeShowsByCategoryID(categoryId: Int): Flow<Resource<List<SelectShowsByCategory>>>

    fun observePagedShowsByCategoryID(categoryId: Int): CommonFlow<PagingData<SelectShowsByCategory>>

    suspend fun updateFollowedShow(traktId: Int, addToWatchList: Boolean)

    suspend fun syncFollowedShows()

    suspend fun syncDiscoverShows()

}