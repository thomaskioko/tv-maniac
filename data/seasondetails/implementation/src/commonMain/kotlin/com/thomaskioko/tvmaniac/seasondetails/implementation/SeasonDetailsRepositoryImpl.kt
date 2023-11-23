package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.db.SeasonEpisodeDetailsById
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.get
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class SeasonDetailsRepositoryImpl(
    private val seasonDetailsStore: SeasonDetailsStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatcher: AppCoroutineDispatchers,
) : SeasonDetailsRepository {

    override fun observeSeasonDetailsStream(
        traktId: Long,
    ): Flow<Either<Failure, List<SeasonEpisodeDetailsById>>> =
        seasonDetailsStore.stream(
            StoreReadRequest.cached(
                key = traktId,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = traktId,
                    requestType = "SEASON_DETAILS",
                    threshold = 3.days,
                ),
            ),
        )
            .distinctUntilChanged()
            .flatMapLatest {
                val data = it.dataOrNull()
                if (data != null) {
                    flowOf(Either.Right(data))
                } else {
                    emptyFlow()
                }
            }
            .flowOn(dispatcher.io)

    override suspend fun fetchSeasonDetails(traktId: Long): List<SeasonEpisodeDetailsById> =
        seasonDetailsStore.get(traktId)
}
