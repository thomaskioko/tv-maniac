package com.thomaskioko.tvmaniac.data.showdetails.api

import com.thomaskioko.tvmaniac.db.TvshowDetails
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.flow.Flow

interface ShowDetailsRepository {
  fun observeShowDetails(
    id: Long,
    forceReload: Boolean = false
  ): Flow<Either<Failure, TvshowDetails>>
}
