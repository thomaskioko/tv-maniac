package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.TvshowDetails
import com.thomaskioko.tvmaniac.core.networkutil.mapToEither
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SHOW_DETAILS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultShowDetailsRepository(
  private val showStore: ShowDetailsStore,
  private val requestManagerRepository: RequestManagerRepository,
  private val dispatchers: AppCoroutineDispatchers,
) : ShowDetailsRepository {

  override fun observeShowDetails(
    id: Long,
    forceReload: Boolean
  ): Flow<Either<Failure, TvshowDetails>> {
    return showStore
      .stream(
        StoreReadRequest.cached(
          key = id,
          refresh =
            forceReload ||
              requestManagerRepository.isRequestExpired(
                entityId = id,
                requestType = SHOW_DETAILS.name,
                threshold = SHOW_DETAILS.duration,
              ),
        ),
      )
      .mapToEither()
      .flowOn(dispatchers.io)
  }
}
