package com.thomaskioko.tvmaniac.core.notifications.implementation

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

public class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in REARM_ACTIONS) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                rearmAlarms(context)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to re-arm notification alarms after boot: ${e.message}")
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun rearmAlarms(context: Context) {
        val store = PendingNotificationsStore(context)
        store.cleanupStaleNotifications()

        val notifications = store.getNotifications()
        if (notifications.isEmpty()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = System.currentTimeMillis()
        notifications.forEach { notification ->
            alarmManager.setWindow(
                AlarmManager.RTC_WAKEUP,
                notification.scheduledTime.coerceAtLeast(now),
                AndroidNotificationManager.ALARM_WINDOW_LENGTH.inWholeMilliseconds,
                notification.buildPendingIntent(context),
            )
        }
        Log.d(TAG, "Re-armed ${notifications.size} notification alarms after boot")
    }

    private companion object {
        private const val TAG = "BootCompletedReceiver"
        private val REARM_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON",
        )
    }
}
