package com.thomaskioko.tvmaniac.lists.api

import kotlinx.coroutines.flow.Flow

public interface ListRepository {

    public fun observeLists(): Flow<List<UserListEntity>>

    public fun observeListsForShow(showId: Long): Flow<List<UserList>>

    public suspend fun fetchUserLists(slug: String, forceRefresh: Boolean = false)

    public suspend fun createList(slug: String, name: String)

    public suspend fun toggleShowInList(slug: String, listId: Long, showId: Long, isCurrentlyInList: Boolean)

    public suspend fun countPendingListShows(): Long
}
