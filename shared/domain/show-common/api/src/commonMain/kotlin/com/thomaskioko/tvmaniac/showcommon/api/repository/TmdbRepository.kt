package com.thomaskioko.tvmaniac.showcommon.api.repository

import com.kuuurt.paging.multiplatform.PagingData
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.CommonFlow
import com.thomaskioko.tvmaniac.core.util.network.Resource
import kotlinx.coroutines.flow.Flow

interface TmdbRepository {

    fun observeShow(tvShowId: Long): Flow<Resource<Show>>

    fun observePagedShowsByCategoryID(
        categoryId: Int
    ): CommonFlow<PagingData<Show>>
}
