package com.thomaskioko.tvmaniac.domain.notifications.interactor

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
public class SyncTraktCalendarInteractor(
    private val episodeRepository: EpisodeRepository,
    private val dateTimeProvider: DateTimeProvider,
    private val logger: Logger,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<SyncTraktCalendarInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        logger.debug(TAG, "Starting Trakt calendar sync")
        withContext(dispatchers.io) {
            episodeRepository.syncUpcomingEpisodesFromTrakt(
                startDate = dateTimeProvider.todayAsIsoDate(),
                days = params.days,
                forceRefresh = params.forceRefresh,
            )
        }
    }

    public data class Params(
        val days: Int = 7,
        val forceRefresh: Boolean = false,
    )

    private companion object {
        private const val TAG = "SyncTraktCalendar"
    }
}
