package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.db.Season_images
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SEASON_DETAILS
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import com.thomaskioko.tvmaniac.util.extensions.mapResult
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
class DefaultSeasonDetailsRepository(
    private val seasonDetailsStore: SeasonDetailsStore,
    private val seasonDetailsDao: SeasonDetailsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatcher: AppCoroutineDispatchers,
) : SeasonDetailsRepository {

    override suspend fun fetchSeasonDetails(
        param: SeasonDetailsParam,
    ): SeasonDetailsWithEpisodes = seasonDetailsStore.get(param)

    override fun observeSeasonDetails(
        param: SeasonDetailsParam,
    ): Flow<Either<Failure, SeasonDetailsWithEpisodes>> =
        seasonDetailsStore.stream(
            StoreReadRequest.cached(
                key = param,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = param.seasonId,
                    requestType = SEASON_DETAILS.name,
                    threshold = SEASON_DETAILS.duration,
                ),
            ),
        )
            .mapResult()
            .flowOn(dispatcher.io)

    override fun fetchSeasonImages(id: Long): List<Season_images> =
        seasonDetailsDao.fetchSeasonImages(id)

    override fun observeSeasonImages(id: Long): Flow<List<Season_images>> =
        seasonDetailsDao.observeSeasonImages(id)
}
