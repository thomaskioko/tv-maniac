package com.thomaskioko.tvmaniac.data.showdetails.testing

import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.db.TvshowDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

public class FakeShowDetailsRepository : ShowDetailsRepository {
    private val showDetails = MutableStateFlow<TvshowDetails?>(null)
    private val fetchInvocations = mutableListOf<FetchInvocation>()

    public data class FetchInvocation(val id: Long, val forceRefresh: Boolean)

    public fun setShowDetailsResult(result: TvshowDetails) {
        showDetails.value = result
    }

    public fun fetchInvocations(): List<FetchInvocation> = fetchInvocations.toList()

    public fun clearInvocations() {
        fetchInvocations.clear()
    }

    override suspend fun fetchShowDetails(id: Long, forceRefresh: Boolean) {
        fetchInvocations.add(FetchInvocation(id, forceRefresh))
    }

    override fun observeShowDetails(id: Long): Flow<TvshowDetails> = showDetails.filterNotNull()
}
