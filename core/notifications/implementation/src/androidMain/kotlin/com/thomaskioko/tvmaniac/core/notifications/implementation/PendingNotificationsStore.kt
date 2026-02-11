package com.thomaskioko.tvmaniac.core.notifications.implementation

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.thomaskioko.tvmaniac.core.notifications.api.EpisodeNotification
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationChannel
import com.thomaskioko.tvmaniac.core.notifications.implementation.model.StoredNotification
import kotlinx.serialization.json.Json

internal class PendingNotificationsStore(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    @Synchronized
    internal fun addNotification(notification: EpisodeNotification) {
        val stored = StoredNotification(
            id = notification.id,
            showId = notification.showId,
            seasonId = notification.seasonId,
            showName = notification.showName,
            episodeTitle = notification.episodeTitle,
            seasonNumber = notification.seasonNumber,
            episodeNumber = notification.episodeNumber,
            imageUrl = notification.imageUrl,
            scheduledTime = notification.scheduledTime,
            channelId = notification.channel.id,
        )
        val notifications = getStoredNotifications().toMutableList()
        notifications.removeAll { it.id == notification.id }
        notifications.add(stored)
        saveNotifications(notifications)
    }

    @Synchronized
    internal fun removeNotification(notificationId: Long) {
        val notifications = getStoredNotifications().toMutableList()
        notifications.removeAll { it.id == notificationId }
        saveNotifications(notifications)
    }

    internal fun getNotificationById(notificationId: Long): EpisodeNotification? {
        return getStoredNotifications()
            .find { it.id == notificationId }
            ?.toEpisodeNotification()
    }

    internal fun getNotifications(): List<EpisodeNotification> {
        return getStoredNotifications().map { it.toEpisodeNotification() }
    }

    @Synchronized
    internal fun cleanupStaleNotifications(): List<Long> {
        val currentTime = System.currentTimeMillis()
        val storedNotifications = getStoredNotifications()
        val (valid, stale) = storedNotifications.partition { notification ->
            notification.scheduledTime + STALE_THRESHOLD_MS > currentTime
        }

        if (stale.isNotEmpty()) {
            saveNotifications(valid)
        }

        return stale.map { it.id }
    }

    @Synchronized
    internal fun clearAllNotifications() {
        prefs.edit { remove(KEY_NOTIFICATIONS) }
    }

    private fun getStoredNotifications(): List<StoredNotification> {
        val jsonString = prefs.getString(KEY_NOTIFICATIONS, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<StoredNotification>>(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to deserialize pending notifications, clearing corrupted data", e)
            prefs.edit { remove(KEY_NOTIFICATIONS) }
            emptyList()
        }
    }

    private fun saveNotifications(notifications: List<StoredNotification>) {
        val jsonString = json.encodeToString(notifications)
        prefs.edit { putString(KEY_NOTIFICATIONS, jsonString) }
    }

    private fun StoredNotification.toEpisodeNotification(): EpisodeNotification =
        EpisodeNotification(
            id = id,
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

    private companion object {
        private const val TAG = "PendingNotificationsStore"
        private const val PREFS_NAME = "episode_notifications"
        private const val KEY_NOTIFICATIONS = "pending_notifications"
        private const val STALE_THRESHOLD_MS = 24 * 60 * 60 * 1000L
    }
}
