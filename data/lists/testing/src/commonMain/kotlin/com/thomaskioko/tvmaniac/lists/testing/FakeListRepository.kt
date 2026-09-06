package com.thomaskioko.tvmaniac.lists.testing

import com.thomaskioko.tvmaniac.lists.api.ListRepository
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

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [DefaultListRepository::class])
public class FakeListRepository : ListRepository {

    private val listsFlow = MutableStateFlow<List<UserListEntity>>(emptyList())
    private val listsWithMembershipFlow = MutableStateFlow<List<UserList>>(emptyList())
    private var listsAfterSync: List<UserList>? = null
    private var toggleGate: CompletableDeferred<Unit>? = null

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

    public fun setListsForShow(lists: List<UserList>) {
        listsWithMembershipFlow.value = lists
    }

    public fun setListsAfterSync(lists: List<UserList>) {
        listsAfterSync = lists
    }

    public fun setToggleGate(gate: CompletableDeferred<Unit>?) {
        toggleGate = gate
    }

    override fun observeLists(): Flow<List<UserListEntity>> = listsFlow.asStateFlow()

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
}
