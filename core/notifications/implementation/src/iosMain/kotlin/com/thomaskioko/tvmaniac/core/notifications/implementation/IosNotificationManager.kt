package com.thomaskioko.tvmaniac.core.notifications.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.notifications.api.EpisodeNotification
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationChannel
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusEphemeral
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationTrigger
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.coroutines.resume

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class IosNotificationManager(
    private val logger: Logger,
) : NotificationManager {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun scheduleNotification(notification: EpisodeNotification) {
        if (!hasNotificationPermission()) {
            logger.debug(TAG, "Skipping notification scheduling - permission not granted")
            return
        }

        val content = UNMutableNotificationContent().apply {
            setTitle(notification.title)
            setBody(notification.message)
            setUserInfo(
                mapOf(
                    KEY_NOTIFICATION_ID to notification.id,
                    KEY_SHOW_ID to notification.showId,
                    KEY_SEASON_ID to notification.seasonId,
                    KEY_SHOW_NAME to notification.showName,
                    KEY_EPISODE_TITLE to notification.episodeTitle,
                    KEY_SEASON_NUMBER to notification.seasonNumber,
                    KEY_EPISODE_NUMBER to notification.episodeNumber,
                    KEY_IMAGE_URL to (notification.imageUrl ?: ""),
                    KEY_SCHEDULED_TIME to notification.scheduledTime,
                    KEY_CHANNEL_ID to notification.channel.id,
                ),
            )
        }

        val currentTime = Clock.System.now().toEpochMilliseconds()
        val trigger: UNNotificationTrigger? = if (notification.scheduledTime <= currentTime + IMMEDIATE_THRESHOLD_MS) {
            UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
                timeInterval = 1.0,
                repeats = false,
            )
        } else {
            val date = NSDate.dateWithTimeIntervalSince1970(notification.scheduledTime / 1000.0)
            val dateComponents = platform.Foundation.NSCalendar.currentCalendar.components(
                platform.Foundation.NSCalendarUnitYear or
                    platform.Foundation.NSCalendarUnitMonth or
                    platform.Foundation.NSCalendarUnitDay or
                    platform.Foundation.NSCalendarUnitHour or
                    platform.Foundation.NSCalendarUnitMinute or
                    platform.Foundation.NSCalendarUnitSecond,
                fromDate = date,
            )
            UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                dateComponents = dateComponents,
                repeats = false,
            )
        }

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = notification.id.toString(),
            content = content,
            trigger = trigger,
        )

        suspendCancellableCoroutine { continuation ->
            notificationCenter.addNotificationRequest(request) { error ->
                if (error != null) {
                    logger.error(TAG, "Failed to schedule notification: ${error.localizedDescription}")
                } else {
                    logger.debug(TAG, "Scheduled notification for ${notification.showName} S${notification.seasonNumber}E${notification.episodeNumber}")
                }
                continuation.resume(Unit)
            }
        }
    }

    override suspend fun cancelNotification(notificationId: Long) {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(
            listOf(notificationId.toString()),
        )
        logger.debug(TAG, "Cancelled notification $notificationId")
    }

    override suspend fun cancelNotificationsForShow(showId: Long) {
        val pendingNotifications = getPendingNotifications()
        val idsToCancel = pendingNotifications
            .filter { it.showId == showId }
            .map { it.id.toString() }

        if (idsToCancel.isNotEmpty()) {
            notificationCenter.removePendingNotificationRequestsWithIdentifiers(idsToCancel)
        }
        logger.debug(TAG, "Cancelled notifications for show $showId")
    }

    override suspend fun cancelAllNotifications() {
        notificationCenter.removeAllPendingNotificationRequests()
        logger.debug(TAG, "Cancelled all notifications")
    }

    override suspend fun getPendingNotifications(): List<EpisodeNotification> =
        suspendCancellableCoroutine { continuation ->
            notificationCenter.getPendingNotificationRequestsWithCompletionHandler { requests ->
                val notifications = requests?.mapNotNull { request ->
                    val req = request as? UNNotificationRequest ?: return@mapNotNull null
                    val userInfo = req.content.userInfo

                    val notificationId = userInfo.getLongValue(KEY_NOTIFICATION_ID) ?: return@mapNotNull null
                    val showId = userInfo.getLongValue(KEY_SHOW_ID) ?: return@mapNotNull null
                    val seasonId = userInfo.getLongValue(KEY_SEASON_ID) ?: 0L
                    val showName = (userInfo[KEY_SHOW_NAME] as? String) ?: ""
                    val episodeTitle = (userInfo[KEY_EPISODE_TITLE] as? String) ?: req.content.body
                    val seasonNumber = userInfo.getLongValue(KEY_SEASON_NUMBER) ?: 0L
                    val episodeNumber = userInfo.getLongValue(KEY_EPISODE_NUMBER) ?: 0L
                    val imageUrl = (userInfo[KEY_IMAGE_URL] as? String)?.takeIf { it.isNotEmpty() }
                    val scheduledTime = userInfo.getLongValue(KEY_SCHEDULED_TIME) ?: 0L
                    val channelId = (userInfo[KEY_CHANNEL_ID] as? String) ?: NotificationChannel.EPISODES_AIRING.id

                    EpisodeNotification(
                        id = notificationId,
                        showId = showId,
                        seasonId = seasonId,
                        showName = showName,
                        episodeTitle = episodeTitle,
                        seasonNumber = seasonNumber,
                        episodeNumber = episodeNumber,
                        imageUrl = imageUrl,
                        scheduledTime = scheduledTime,
                        channel = NotificationChannel.fromId(channelId),
                    )
                } ?: emptyList()
                continuation.resume(notifications)
            }
        }

    private suspend fun hasNotificationPermission(): Boolean =
        suspendCancellableCoroutine { continuation ->
            notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
                val status = settings?.authorizationStatus
                val hasPermission = status == UNAuthorizationStatusAuthorized ||
                    status == UNAuthorizationStatusProvisional ||
                    status == UNAuthorizationStatusEphemeral
                continuation.resume(hasPermission)
            }
        }

    private fun Map<Any?, *>.getLongValue(key: String): Long? {
        return when (val value = this[key]) {
            is Long -> value
            is Number -> value.toLong()
            else -> null
        }
    }

    private companion object {
        private const val TAG = "IosNotificationManager"
        private const val IMMEDIATE_THRESHOLD_MS = 5000L
        private const val KEY_NOTIFICATION_ID = "notification_id"
        private const val KEY_SHOW_ID = "show_id"
        private const val KEY_SEASON_ID = "season_id"
        private const val KEY_SHOW_NAME = "show_name"
        private const val KEY_EPISODE_TITLE = "episode_title"
        private const val KEY_SEASON_NUMBER = "season_number"
        private const val KEY_EPISODE_NUMBER = "episode_number"
        private const val KEY_IMAGE_URL = "image_url"
        private const val KEY_SCHEDULED_TIME = "scheduled_time"
        private const val KEY_CHANNEL_ID = "channel_id"
    }
}
