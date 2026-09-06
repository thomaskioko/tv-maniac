package com.thomaskioko.tvmaniac.lists.api

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

public interface ListRepository {

    public fun observeLists(): Flow<List<UserListEntity>>

    public fun observeListsForShow(showId: Long): Flow<List<UserList>>

    public fun observePagedListShows(listId: Long): Flow<PagingData<ListShowItem>>

    public suspend fun getTmdbIdsMissingPoster(listId: Long): List<Long>

    public suspend fun fetchUserLists(slug: String, forceRefresh: Boolean = false)

    public suspend fun createList(name: String, traktSlug: String?)

    public suspend fun toggleShowInList(listId: Long, showId: Long, isCurrentlyInList: Boolean, traktSlug: String?)

    public suspend fun syncPendingLists(slug: String)

    public suspend fun countPendingListShows(): Long

    public suspend fun countPendingLists(): Long
}
