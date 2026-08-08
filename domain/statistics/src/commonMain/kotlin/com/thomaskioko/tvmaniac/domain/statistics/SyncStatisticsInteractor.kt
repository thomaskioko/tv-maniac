package com.thomaskioko.tvmaniac.domain.statistics

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

@Inject
@SingleIn(AppScope::class)
public class SyncStatisticsInteractor(
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val ratingsRepository: RatingsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<SyncStatisticsInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {
            val episodeFailure = try {
                watchedEpisodeSyncRepository.syncAllWatchedEpisodes(forceRefresh = params.forceRefresh)
                null
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                throwable
            }

            ratingsRepository.syncUserRatings()

            if (episodeFailure != null) throw episodeFailure
        }
    }

    public data class Params(
        val forceRefresh: Boolean = false,
    )
}
