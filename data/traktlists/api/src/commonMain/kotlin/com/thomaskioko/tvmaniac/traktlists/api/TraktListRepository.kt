package com.thomaskioko.tvmaniac.traktlists.api

import kotlinx.coroutines.flow.Flow

public interface TraktListRepository {

    public fun observeLists(): Flow<List<TraktListEntity>>

    public suspend fun syncLists(forceRefresh: Boolean = false)
}
