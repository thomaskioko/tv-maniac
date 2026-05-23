package com.thomaskioko.tvmaniac.traktlists.testing

import com.thomaskioko.tvmaniac.traktlists.api.TraktList
import com.thomaskioko.tvmaniac.traktlists.api.TraktListEntity
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import com.thomaskioko.tvmaniac.traktlists.implementation.DefaultTraktListRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [DefaultTraktListRepository::class])
public class FakeTraktListRepository : TraktListRepository {

    private val listsFlow = MutableStateFlow<List<TraktListEntity>>(emptyList())
    private val listsWithMembershipFlow = MutableStateFlow<List<TraktList>>(emptyList())
    private var listsAfterSync: List<TraktList>? = null
    private var toggleGate: CompletableDeferred<Unit>? = null

    public var fetchUserListsInvocations: Int = 0
        private set

    public var toggleShowInListInvocations: Int = 0
        private set

    public fun setLists(lists: List<TraktListEntity>) {
        listsFlow.value = lists
    }

    public fun setListsForShow(lists: List<TraktList>) {
        listsWithMembershipFlow.value = lists
    }

    public fun setListsAfterSync(lists: List<TraktList>) {
        listsAfterSync = lists
    }

    public fun setToggleGate(gate: CompletableDeferred<Unit>?) {
        toggleGate = gate
    }

    override fun observeLists(): Flow<List<TraktListEntity>> = listsFlow.asStateFlow()

    override fun observeListsForShow(traktShowId: Long): Flow<List<TraktList>> =
        listsWithMembershipFlow.asStateFlow()

    override suspend fun fetchUserLists(slug: String, forceRefresh: Boolean) {
        fetchUserListsInvocations += 1
        listsAfterSync?.let { listsWithMembershipFlow.value = it }
    }

    override suspend fun createList(slug: String, name: String) {
    }

    override suspend fun toggleShowInList(slug: String, listId: Long, traktShowId: Long, isCurrentlyInList: Boolean) {
        toggleShowInListInvocations += 1
        toggleGate?.await()
    }
}
