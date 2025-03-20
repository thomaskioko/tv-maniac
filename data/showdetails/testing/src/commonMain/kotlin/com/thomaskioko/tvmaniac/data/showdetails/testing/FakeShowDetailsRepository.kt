package com.thomaskioko.tvmaniac.data.showdetails.testing

import com.thomaskioko.tvmaniac.db.TvshowDetails
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class FakeShowDetailsRepository : ShowDetailsRepository {
  private val showDetails = MutableStateFlow<Either<Failure, TvshowDetails>?>(null)

  fun setShowDetailsResult(result: Either<Failure, TvshowDetails>) {
    showDetails.value = result
  }

  override fun observeShowDetails(
    id: Long,
    forceReload: Boolean
  ): Flow<Either<Failure, TvshowDetails>> {
    return showDetails.filterNotNull()
  }
}
