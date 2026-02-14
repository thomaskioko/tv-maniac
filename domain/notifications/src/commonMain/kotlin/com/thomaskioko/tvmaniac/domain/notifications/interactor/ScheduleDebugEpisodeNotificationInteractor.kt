package com.thomaskioko.tvmaniac.domain.notifications.interactor

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.notifications.api.EpisodeNotification
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationChannel
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

@Inject
@SingleIn(AppScope::class)
public class ScheduleDebugEpisodeNotificationInteractor(
    private val episodeRepository: EpisodeRepository,
    private val notificationManager: NotificationManager,
    private val dateTimeProvider: DateTimeProvider,
    private val logger: Logger,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<ScheduleDebugEpisodeNotificationInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {
            val upcomingEpisodes = episodeRepository.getUpcomingEpisodesFromFollowedShows(
                limit = 365.days,
            )

            val scheduledTime = dateTimeProvider.nowMillis() + params.delay.inWholeMilliseconds

            val notification = if (upcomingEpisodes.isNotEmpty()) {
                val episode = upcomingEpisodes.random()
                EpisodeNotification(
                    id = episode.episodeId,
                    showId = episode.showId,
                    seasonId = episode.seasonId,
                    showName = episode.showName,
                    episodeTitle = episode.title ?: "Episode ${episode.episodeNumber}",
                    seasonNumber = episode.seasonNumber,
                    episodeNumber = episode.episodeNumber,
                    imageUrl = episode.showPoster,
                    scheduledTime = scheduledTime,
                    channel = NotificationChannel.DEVELOPER,
                )
            } else {
                EpisodeNotification(
                    id = dateTimeProvider.nowMillis(),
                    showId = 0L,
                    seasonId = 0L,
                    showName = "Test Show",
                    episodeTitle = "Test Episode - Debug Notification",
                    seasonNumber = 1,
                    episodeNumber = 1,
                    imageUrl = null,
                    scheduledTime = scheduledTime,
                    channel = NotificationChannel.DEVELOPER,
                )
            }

            notificationManager.scheduleNotification(notification)
            logger.debug(TAG, "Triggered debug notification: ${notification.showName}")
        }
    }

    public data class Params(
        val delay: Duration = Duration.ZERO,
    )

    private companion object {
        private const val TAG = "ScheduleDebugEpisodeNotification"
    }
}
