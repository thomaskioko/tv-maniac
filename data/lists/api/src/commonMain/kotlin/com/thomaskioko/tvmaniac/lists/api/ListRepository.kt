package com.thomaskioko.tvmaniac.lists.api

import kotlinx.coroutines.flow.Flow

public interface ListRepository {

    public fun observeLists(): Flow<List<UserListEntity>>

    public fun observeListsForShow(showId: Long): Flow<List<UserList>>

    public suspend fun fetchUserLists(slug: String, forceRefresh: Boolean = false)

    public suspend fun createList(name: String, traktSlug: String?)

    public suspend fun toggleShowInList(listId: Long, showId: Long, isCurrentlyInList: Boolean, traktSlug: String?)

    public suspend fun countPendingListShows(): Long
}
