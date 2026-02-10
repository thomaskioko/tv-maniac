package com.thomaskioko.tvmaniac.core.notifications.implementation

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.notifications.api.EpisodeNotification
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationChannel
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationIconProvider
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration.Companion.minutes
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager as AppNotificationManager

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidNotificationManager(
    private val context: Context,
    private val notificationIconProvider: NotificationIconProvider,
    private val logger: Logger,
) : AppNotificationManager {

    private val pendingNotificationsStore = PendingNotificationsStore(context)

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private val notificationManagerCompat: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(context)
    }

    init {
        iconProvider = notificationIconProvider
        NotificationChannel.entries.forEach { createNotificationChannel(it) }
    }

    override suspend fun scheduleNotification(notification: EpisodeNotification) {
        if (!notificationManagerCompat.areNotificationsEnabled()) {
            logger.debug(TAG, "Skipping notification scheduling - notifications not enabled")
            return
        }

        createNotificationChannel(notification.channel)
        pendingNotificationsStore.addNotification(notification)

        val windowStartTime = notification.scheduledTime - ALARM_WINDOW_LENGTH.inWholeMilliseconds
        if (windowStartTime <= System.currentTimeMillis()) {
            showNotificationImmediately(notification)
            logger.debug(TAG, "Notification for ${notification.showName} is past-due, showing immediately")
            return
        }

        logger.debug(TAG, "Scheduling notification for ${notification.showName} S${notification.seasonNumber}E${notification.episodeNumber}")

        alarmManager.setWindow(
            AlarmManager.RTC_WAKEUP,
            windowStartTime,
            ALARM_WINDOW_LENGTH.inWholeMilliseconds,
            notification.buildPendingIntent(context),
        )
    }

    @SuppressLint("MissingPermission")
    private fun showNotificationImmediately(notification: EpisodeNotification) {
        val contentIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(AppNotificationManager.EXTRA_FROM_NOTIFICATION, true)
            putExtra(AppNotificationManager.EXTRA_SHOW_ID, notification.showId)
            putExtra(AppNotificationManager.EXTRA_SEASON_ID, notification.seasonId)
            putExtra(AppNotificationManager.EXTRA_SEASON_NUMBER, notification.seasonNumber)
        }

        val pendingContentIntent = contentIntent?.let {
            PendingIntent.getActivity(
                context,
                notification.showId.toInt(),
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        val androidNotification = NotificationCompat.Builder(context, notification.channel.id)
            .setSmallIcon(notificationIconProvider.smallIconResId)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .apply {
                pendingContentIntent?.let { setContentIntent(it) }
            }
            .build()

        try {
            notificationManagerCompat.notify(notification.id.toInt(), androidNotification)
            logger.debug(TAG, "Notification displayed successfully: ${notification.showName}")
        } catch (se: SecurityException) {
            logger.error(TAG, "Failed to post notification - missing POST_NOTIFICATIONS permission: ${se.message}")
        }
    }

    override suspend fun cancelNotification(notificationId: Long) {
        pendingNotificationsStore.removeNotification(notificationId)

        val intent = EpisodeNotificationReceiver.buildIntent(context, notificationId)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
        logger.debug(TAG, "Cancelled notification $notificationId")
    }

    override suspend fun cancelNotificationsForShow(showId: Long) {
        val pendingNotifications = pendingNotificationsStore.getNotifications()
        pendingNotifications
            .filter { it.showId == showId }
            .forEach { cancelNotification(it.id) }
        logger.debug(TAG, "Cancelled notifications for show $showId")
    }

    override suspend fun cancelAllNotifications() {
        val pendingNotifications = pendingNotificationsStore.getNotifications()
        pendingNotifications.forEach { notification ->
            try {
                cancelAlarmOnly(notification.id)
            } catch (e: Exception) {
                logger.error(TAG, "Failed to cancel alarm for notification ${notification.id}: ${e.message}")
            }
        }
        pendingNotificationsStore.clearAllNotifications()
        logger.debug(TAG, "Cancelled all notifications")
    }

    override suspend fun getPendingNotifications(): List<EpisodeNotification> {
        val staleIds = pendingNotificationsStore.cleanupStaleNotifications()
        staleIds.forEach { notificationId ->
            cancelAlarmOnly(notificationId)
        }
        if (staleIds.isNotEmpty()) {
            logger.debug(TAG, "Cleaned up ${staleIds.size} stale notification alarms")
        }
        return pendingNotificationsStore.getNotifications()
    }

    private fun cancelAlarmOnly(notificationId: Long) {
        val intent = EpisodeNotificationReceiver.buildIntent(context, notificationId)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    private fun createNotificationChannel(channel: NotificationChannel) {
        val androidChannel = NotificationChannelCompat.Builder(channel.id, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .apply {
                when (channel) {
                    NotificationChannel.EPISODES_AIRING -> {
                        setName(CHANNEL_NAME_EPISODES)
                        setDescription(CHANNEL_DESCRIPTION_EPISODES)
                        setVibrationEnabled(true)
                    }
                    NotificationChannel.DEVELOPER -> {
                        setName(CHANNEL_NAME_DEVELOPER)
                        setDescription(CHANNEL_DESCRIPTION_DEVELOPER)
                        setVibrationEnabled(true)
                    }
                }
            }
            .build()
        notificationManagerCompat.createNotificationChannel(androidChannel)
    }

    internal companion object {
        internal const val TAG = "AndroidNotificationManager"
        private const val CHANNEL_NAME_EPISODES = "Episode Notifications"
        private const val CHANNEL_DESCRIPTION_EPISODES = "Notifications for upcoming episodes"
        private const val CHANNEL_NAME_DEVELOPER = "Developer Testing"
        private const val CHANNEL_DESCRIPTION_DEVELOPER = "Notifications for testing"
        private val ALARM_WINDOW_LENGTH = 10.minutes

        @Volatile
        internal var iconProvider: NotificationIconProvider? = null
    }
}
