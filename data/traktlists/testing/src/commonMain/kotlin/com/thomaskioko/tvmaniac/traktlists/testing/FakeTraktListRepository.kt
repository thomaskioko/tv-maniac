package com.thomaskioko.tvmaniac.traktlists.testing

import com.thomaskioko.tvmaniac.traktlists.api.TraktList
import com.thomaskioko.tvmaniac.traktlists.api.TraktListEntity
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeTraktListRepository : TraktListRepository {

    private val listsFlow = MutableStateFlow<List<TraktListEntity>>(emptyList())
    private val listsWithMembershipFlow = MutableStateFlow<List<TraktList>>(emptyList())

    public fun setLists(lists: List<TraktListEntity>) {
        listsFlow.value = lists
    }

    public fun setListsForShow(lists: List<TraktList>) {
        listsWithMembershipFlow.value = lists
    }

    override fun observeLists(): Flow<List<TraktListEntity>> = listsFlow.asStateFlow()

    override fun observeListsForShow(traktShowId: Long): Flow<List<TraktList>> =
        listsWithMembershipFlow.asStateFlow()

    override suspend fun fetchUserLists(slug: String, forceRefresh: Boolean) {
    }

    override suspend fun createList(slug: String, name: String) {
    }

    override suspend fun toggleShowInList(slug: String, listId: Long, traktShowId: Long, isCurrentlyInList: Boolean) {
    }
}
