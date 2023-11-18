package com.thomaskioko.tvmaniac.profilestats.implementation

import com.thomaskioko.tvmaniac.core.db.Stats
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.profilestats.api.StatsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class StatsRepositoryImpl(
    private val store: StatsStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : StatsRepository {

    override fun observeStats(slug: String): Flow<Either<Failure, Stats>> =
        store.stream(
            StoreReadRequest.cached(
                key = slug,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = 0,
                    requestType = "USER_STATS",
                    threshold = 6.days,
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
            .flowOn(dispatchers.io)
}
