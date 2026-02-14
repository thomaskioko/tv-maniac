package com.thomaskioko.tvmaniac.core.notifications.api

public interface NotificationManager {
    public suspend fun scheduleNotification(notification: EpisodeNotification)
    public suspend fun cancelNotification(notificationId: Long)
    public suspend fun cancelNotificationsForShow(showId: Long)
    public suspend fun cancelAllNotifications()
    public suspend fun getPendingNotifications(): List<EpisodeNotification>

    public companion object {
        public const val EXTRA_FROM_NOTIFICATION: String = "extra_from_notification"
        public const val EXTRA_SHOW_ID: String = "show_id"
        public const val EXTRA_SEASON_ID: String = "season_id"
        public const val EXTRA_SEASON_NUMBER: String = "season_number"
    }
}
