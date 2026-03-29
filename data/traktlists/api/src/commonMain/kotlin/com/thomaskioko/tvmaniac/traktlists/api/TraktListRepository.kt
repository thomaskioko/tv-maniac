package com.thomaskioko.tvmaniac.traktlists.api

import kotlinx.coroutines.flow.Flow

public interface TraktListRepository {

    public fun observeLists(): Flow<List<TraktListEntity>>

    public fun observeListsForShow(traktShowId: Long): Flow<List<TraktListWithMembership>>

    public suspend fun syncLists(forceRefresh: Boolean = false)

    public suspend fun createList(name: String)

    public suspend fun toggleShowInList(listId: Long, traktShowId: Long, isCurrentlyInList: Boolean)
}
