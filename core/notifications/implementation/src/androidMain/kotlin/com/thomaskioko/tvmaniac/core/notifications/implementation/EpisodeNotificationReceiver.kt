package com.thomaskioko.tvmaniac.core.notifications.implementation

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.thomaskioko.tvmaniac.core.notifications.api.EpisodeNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager as AppNotificationManager

public class EpisodeNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getLongExtra(EXTRA_NOTIFICATION_ID, -1)
        if (notificationId == -1L) {
            Log.e(TAG, "Received broadcast with invalid notification ID")
            return
        }

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val store = PendingNotificationsStore(context)
                val notification = store.getNotificationById(notificationId)

                if (notification == null) {
                    Log.d(TAG, "Notification $notificationId not found in store (already triggered or cancelled)")
                    pendingResult.finish()
                    return@launch
                }

                showNotification(context, notification)
                store.removeNotification(notificationId)
            } catch (e: Exception) {
                Log.e(TAG, "Error showing notification: ${e.message}")
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, notification: EpisodeNotification) {
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

        val iconResId = AndroidNotificationManager.iconProvider?.smallIconResId
            ?: android.R.drawable.ic_popup_reminder

        val androidNotification = NotificationCompat.Builder(context, notification.channel.id)
            .setSmallIcon(iconResId)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .apply {
                pendingContentIntent?.let { setContentIntent(it) }
            }
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notification.id.toInt(), androidNotification)
    }

    internal companion object {
        private const val TAG = "EpisodeNotificationReceiver"
        private const val EXTRA_NOTIFICATION_ID = "notification_id"

        internal fun buildIntent(context: Context, notificationId: Long): Intent {
            return Intent(context, EpisodeNotificationReceiver::class.java)
                .putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
    }
}

private const val PENDING_INTENT_FLAGS =
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

internal fun EpisodeNotification.buildPendingIntent(context: Context): PendingIntent {
    val intent = EpisodeNotificationReceiver.buildIntent(context, id)
    return PendingIntent.getBroadcast(context, id.toInt(), intent, PENDING_INTENT_FLAGS)
}
