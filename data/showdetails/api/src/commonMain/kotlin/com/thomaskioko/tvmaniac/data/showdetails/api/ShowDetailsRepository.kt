package com.thomaskioko.tvmaniac.data.showdetails.api

import com.thomaskioko.tvmaniac.db.TvshowDetails
import kotlinx.coroutines.flow.Flow

public interface ShowDetailsRepository {
    public suspend fun fetchShowDetails(
        id: Long,
        forceRefresh: Boolean = false,
    )

    public fun observeShowDetails(id: Long): Flow<TvshowDetails>
}
