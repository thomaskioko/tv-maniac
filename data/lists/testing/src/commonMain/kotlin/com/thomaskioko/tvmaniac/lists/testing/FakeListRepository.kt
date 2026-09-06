package com.thomaskioko.tvmaniac.lists.testing

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.lists.api.ListRepository
import com.thomaskioko.tvmaniac.lists.api.ListShowItem
import com.thomaskioko.tvmaniac.lists.api.UserList
import com.thomaskioko.tvmaniac.lists.api.UserListEntity
import com.thomaskioko.tvmaniac.lists.implementation.DefaultListRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [DefaultListRepository::class])
public class FakeListRepository : ListRepository {

    private val listsFlow = MutableStateFlow<List<UserListEntity>>(emptyList())
    private val listsWithMembershipFlow = MutableStateFlow<List<UserList>>(emptyList())
    private val pagedShowsFlow = MutableStateFlow(PagingData.empty<ListShowItem>())
    private var listsAfterSync: List<UserList>? = null
    private var toggleGate: CompletableDeferred<Unit>? = null
    private var observeError: Throwable? = null
    private var tmdbIdsMissingPoster: List<Long> = emptyList()

    public var fetchUserListsInvocations: Int = 0
        private set

    public var toggleShowInListInvocations: Int = 0
        private set

    private val createdListNames = mutableListOf<String>()
    private val toggledShows = mutableListOf<Pair<Long, Long>>()
    private var lastCreateTraktSlug: String? = null
    private var lastToggleTraktSlug: String? = null
    private val syncPendingListsCalls = mutableListOf<String>()
    private val callOrder = mutableListOf<String>()

    public fun createdListNames(): List<String> = createdListNames

    public fun toggledShows(): List<Pair<Long, Long>> = toggledShows

    public fun lastCreateTraktSlug(): String? = lastCreateTraktSlug

    public fun lastToggleTraktSlug(): String? = lastToggleTraktSlug

    public fun syncPendingListsCalls(): List<String> = syncPendingListsCalls

    public fun callOrder(): List<String> = callOrder

    public fun setLists(lists: List<UserListEntity>) {
        listsFlow.value = lists
    }

    public fun setObserveError(error: Throwable?) {
        observeError = error
    }

    public fun setListsForShow(lists: List<UserList>) {
        listsWithMembershipFlow.value = lists
    }

    public fun setPagedListShows(pagingData: PagingData<ListShowItem>) {
        pagedShowsFlow.value = pagingData
    }

    public fun setTmdbIdsMissingPoster(tmdbIds: List<Long>) {
        tmdbIdsMissingPoster = tmdbIds
    }

    public fun setListsAfterSync(lists: List<UserList>) {
        listsAfterSync = lists
    }

    public fun setToggleGate(gate: CompletableDeferred<Unit>?) {
        toggleGate = gate
    }

    override fun observeLists(): Flow<List<UserListEntity>> =
        observeError?.let { error -> flow { throw error } } ?: listsFlow.asStateFlow()

    override fun observeListsForShow(showId: Long): Flow<List<UserList>> =
        listsWithMembershipFlow.asStateFlow()

    override suspend fun fetchUserLists(slug: String, forceRefresh: Boolean) {
        callOrder += "fetchUserLists"
        fetchUserListsInvocations += 1
        listsAfterSync?.let { listsWithMembershipFlow.value = it }
    }

    override suspend fun createList(name: String, traktSlug: String?) {
        lastCreateTraktSlug = traktSlug
        createdListNames += name
    }

    override suspend fun toggleShowInList(listId: Long, showId: Long, isCurrentlyInList: Boolean, traktSlug: String?) {
        toggleShowInListInvocations += 1
        lastToggleTraktSlug = traktSlug
        toggledShows += listId to showId
        toggleGate?.await()
    }

    override suspend fun syncPendingLists(slug: String) {
        callOrder += "syncPendingLists"
        syncPendingListsCalls += slug
    }

    private var pendingListShowsCount = 0L
    private var pendingListsCount = 0L

    public fun setPendingListShowsCount(count: Long) {
        pendingListShowsCount = count
    }

    public fun setPendingListsCount(count: Long) {
        pendingListsCount = count
    }

    override suspend fun countPendingListShows(): Long = pendingListShowsCount

    override suspend fun countPendingLists(): Long = pendingListsCount

    override fun observePagedListShows(listId: Long): Flow<PagingData<ListShowItem>> = pagedShowsFlow.asStateFlow()

    override suspend fun getTmdbIdsMissingPoster(listId: Long): List<Long> = tmdbIdsMissingPoster
}
