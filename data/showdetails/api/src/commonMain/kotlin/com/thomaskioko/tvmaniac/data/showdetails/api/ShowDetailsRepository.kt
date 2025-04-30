package com.thomaskioko.tvmaniac.data.showdetails.api

import com.thomaskioko.tvmaniac.db.TvshowDetails
import kotlinx.coroutines.flow.Flow

interface ShowDetailsRepository {
  suspend fun fetchShowDetails(
    id: Long,
    forceRefresh: Boolean = false,
  )

  fun observeShowDetails(id: Long): Flow<TvshowDetails>
}
