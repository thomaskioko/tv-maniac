package com.thomaskioko.tvmaniac.episodeimages.implementation

import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageDao
import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class EpisodeImageRepositoryImpl(
    private val dispatchers: AppCoroutineDispatchers,
    private val requestManagerRepository: RequestManagerRepository,
    private val store: EpisodeImageStore,
    private val episodeImageDao: EpisodeImageDao,
) : EpisodeImageRepository {

    override fun updateEpisodeImage(traktId: Long): Flow<Either<Failure, Unit>> =
        episodeImageDao.observeEpisodeImage(traktId)
            .flatMapLatest {
                store.stream(
                    StoreReadRequest.cached(
                        key = traktId,
                        refresh = requestManagerRepository.isRequestExpired(
                            entityId = traktId,
                            requestType = "EPISODE_IMAGE",
                            threshold = 1.hours,
                        ),
                    ),
                )
            }
            .flatMapLatest { flowOf(Either.Right(Unit)) }
            .flowOn(dispatchers.io)
}
