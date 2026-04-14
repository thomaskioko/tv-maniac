package com.thomaskioko.tvmaniac.traktlists.api

import kotlinx.coroutines.flow.Flow

public interface TraktListRepository {

    public fun observeLists(): Flow<List<TraktListEntity>>

    public fun observeListsForShow(traktShowId: Long): Flow<List<TraktList>>

    public suspend fun fetchUserLists(slug: String, forceRefresh: Boolean = false)

    public suspend fun createList(slug: String, name: String)

    public suspend fun toggleShowInList(slug: String, listId: Long, traktShowId: Long, isCurrentlyInList: Boolean)
}
