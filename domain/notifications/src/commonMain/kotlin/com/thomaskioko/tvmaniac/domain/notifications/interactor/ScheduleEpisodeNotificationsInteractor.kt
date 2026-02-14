package com.thomaskioko.tvmaniac.domain.notifications.interactor

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.notifications.api.EpisodeNotification
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Inject
@SingleIn(AppScope::class)
public class ScheduleEpisodeNotificationsInteractor(
    private val datastoreRepository: DatastoreRepository,
    private val episodeRepository: EpisodeRepository,
    private val notificationManager: NotificationManager,
    private val dateTimeProvider: DateTimeProvider,
    private val logger: Logger,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<ScheduleEpisodeNotificationsInteractor.Params>() {

    public data class Params(
        val limit: Duration = 12.hours,
        val bufferTime: Duration = 10.minutes,
    )

    override suspend fun doWork(params: Params) {
        if (!datastoreRepository.getEpisodeNotificationsEnabled()) {
            logger.debug(TAG, "Episode notifications disabled, skipping scheduling")
            return
        }
        withContext(dispatchers.io) {
            val oldNotificationIds = notificationManager.getPendingNotifications().map { it.id }

            val upcomingEpisodes = episodeRepository.getUpcomingEpisodesFromFollowedShows(
                limit = params.limit,
            )

            logger.debug(TAG, "Found ${upcomingEpisodes.size} upcoming episodes")

            val currentTime = dateTimeProvider.nowMillis()
            val newNotificationIds = mutableSetOf<Long>()

            upcomingEpisodes.forEach { episode ->
                val notificationTime = episode.firstAired - params.bufferTime.inWholeMilliseconds

                if (notificationTime <= currentTime) {
                    logger.debug(TAG, "Skipping ${episode.showName} - notification time already passed")
                    return@forEach
                }

                val notification = EpisodeNotification(
                    id = episode.episodeId,
                    showId = episode.showId,
                    seasonId = episode.seasonId,
                    showName = episode.showName,
                    episodeTitle = episode.title ?: "Episode ${episode.episodeNumber}",
                    seasonNumber = episode.seasonNumber,
                    episodeNumber = episode.episodeNumber,
                    imageUrl = episode.showPoster,
                    scheduledTime = notificationTime,
                )

                notificationManager.scheduleNotification(notification)
                newNotificationIds.add(episode.episodeId)

                logger.debug(
                    TAG,
                    "Scheduled notification for ${episode.showName} S${episode.seasonNumber}E${episode.episodeNumber}",
                )
            }

            val staleIds = oldNotificationIds.filterNot { it in newNotificationIds }
            staleIds.forEach { notificationManager.cancelNotification(it) }

            val pendingCount = notificationManager.getPendingNotifications().size
            logger.debug(
                TAG,
                "Scheduling complete: ${newNotificationIds.size} scheduled, ${staleIds.size} cancelled, $pendingCount pending",
            )
        }
    }

    private companion object {
        private const val TAG = "ScheduleEpisodeNotifications"
    }
}
