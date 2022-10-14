package com.thomaskioko.tvmaniac.shows.api.repository

import com.kuuurt.paging.multiplatform.PagingData
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.CommonFlow
import com.thomaskioko.tvmaniac.core.util.network.Resource
import kotlinx.coroutines.flow.Flow

interface TmdbRepository {

    fun observeShow(tmdbId: Int): Flow<Resource<Show>>

    fun observePagedShowsByCategoryID(
        categoryId: Int
    ): CommonFlow<PagingData<Show>>

    fun observeUpdateShowArtWork() : Flow<Unit>

    suspend fun syncShowArtWork()
}
