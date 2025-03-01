package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.db.Season_images
import com.thomaskioko.tvmaniac.core.networkutil.mapToEither
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SEASON_DETAILS
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultSeasonDetailsRepository(
  private val seasonDetailsStore: SeasonDetailsStore,
  private val seasonDetailsDao: SeasonDetailsDao,
  private val requestManagerRepository: RequestManagerRepository,
) : SeasonDetailsRepository {

  override fun observeSeasonDetails(
    param: SeasonDetailsParam,
  ): Flow<Either<Failure, SeasonDetailsWithEpisodes>> =
    seasonDetailsStore
      .stream(
        StoreReadRequest.cached(
          key = param,
          refresh =
            requestManagerRepository.isRequestExpired(
              entityId = param.seasonId,
              requestType = SEASON_DETAILS.name,
              threshold = SEASON_DETAILS.duration,
            ),
        ),
      )
      .mapToEither()

  override fun observeSeasonImages(id: Long): Flow<List<Season_images>> =
    seasonDetailsDao.observeSeasonImages(id)
}
