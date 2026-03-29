package com.thomaskioko.tvmaniac.traktlists.testing

import com.thomaskioko.tvmaniac.traktlists.api.TraktListEntity
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

public class FakeTraktListRepository : TraktListRepository {

    private val listsFlow = MutableStateFlow<List<TraktListEntity>>(emptyList())

    public fun setLists(lists: List<TraktListEntity>) {
        listsFlow.value = lists
    }

    override fun observeLists(): Flow<List<TraktListEntity>> = listsFlow

    override suspend fun syncLists(forceRefresh: Boolean) {
    }
}
