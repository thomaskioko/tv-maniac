package com.thomaskioko.tvmaniac.details.api.repository

import com.kuuurt.paging.multiplatform.PagingData
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.shared.core.util.CommonFlow
import com.thomaskioko.tvmaniac.shared.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface TvShowsRepository {

    suspend fun updateFollowing(showId: Long, addToWatchList: Boolean)

    fun observeShow(tvShowId: Long): Flow<Resource<Show>>

    fun observeFollowing(): Flow<List<Show>>

    fun observePagedShowsByCategoryID(
        categoryId: Int
    ): CommonFlow<PagingData<Show>>
}
