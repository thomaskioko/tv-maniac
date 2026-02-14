package com.thomaskioko.tvmaniac.core.notifications.testing

import com.thomaskioko.tvmaniac.core.notifications.api.EpisodeNotification
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager

public class FakeNotificationManager : NotificationManager {
    private val scheduledNotifications = mutableMapOf<Long, EpisodeNotification>()

    override suspend fun scheduleNotification(notification: EpisodeNotification) {
        scheduledNotifications[notification.id] = notification
    }

    override suspend fun cancelNotification(notificationId: Long) {
        scheduledNotifications.remove(notificationId)
    }

    override suspend fun cancelNotificationsForShow(showId: Long) {
        val idsToRemove = scheduledNotifications.values
            .filter { it.showId == showId }
            .map { it.id }
        idsToRemove.forEach { scheduledNotifications.remove(it) }
    }

    override suspend fun cancelAllNotifications() {
        scheduledNotifications.clear()
    }

    override suspend fun getPendingNotifications(): List<EpisodeNotification> =
        scheduledNotifications.values.toList()

    public fun getScheduledNotifications(): Map<Long, EpisodeNotification> =
        scheduledNotifications.toMap()

    public fun addPendingNotification(notification: EpisodeNotification) {
        scheduledNotifications[notification.id] = notification
    }

    public fun reset() {
        scheduledNotifications.clear()
    }
}
