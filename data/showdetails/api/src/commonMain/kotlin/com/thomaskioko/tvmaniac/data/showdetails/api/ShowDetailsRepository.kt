package com.thomaskioko.tvmaniac.data.showdetails.api

import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.flow.Flow

interface ShowDetailsRepository {

  suspend fun getShowDetails(id: Long): TvshowDetails

  fun observeShowDetails(id: Long): Flow<Either<Failure, TvshowDetails>>
}
