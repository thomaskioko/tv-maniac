package com.thomaskioko.tvmaniac.data.showdetails.testing

import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.db.TvshowDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

public class FakeShowDetailsRepository : ShowDetailsRepository {
    private val showDetails = MutableStateFlow<TvshowDetails?>(null)

    public fun setShowDetailsResult(result: TvshowDetails) {
        showDetails.value = result
    }

    override suspend fun fetchShowDetails(id: Long, forceRefresh: Boolean) {
    }

    override fun observeShowDetails(id: Long): Flow<TvshowDetails> = showDetails.filterNotNull()
}
