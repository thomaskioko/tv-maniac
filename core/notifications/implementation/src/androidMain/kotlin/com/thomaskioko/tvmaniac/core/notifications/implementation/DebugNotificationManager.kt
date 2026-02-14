package com.thomaskioko.tvmaniac.core.notifications.implementation

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationChannel
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationIconProvider
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
public class DebugNotificationManager(
    private val context: Context,
    private val notificationIconProvider: NotificationIconProvider,
) {

    private val notificationManagerCompat: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(context)
    }

    @SuppressLint("MissingPermission")
    public fun show() {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_DEEP_LINK, DEEP_LINK_DEBUG_MENU)
        } ?: return

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, NotificationChannel.DEVELOPER.id)
            .setSmallIcon(notificationIconProvider.debugIconResId)
            .setContentTitle("Debug Menu")
            .setContentText("Tap to open debug menu")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            notificationManagerCompat.notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
        }
    }

    public fun dismiss() {
        notificationManagerCompat.cancel(NOTIFICATION_ID)
    }

    public companion object {
        public const val EXTRA_DEEP_LINK: String = "extra_deep_link"
        public const val DEEP_LINK_DEBUG_MENU: String = "debug_menu"
        private const val NOTIFICATION_ID: Int = 9999
    }
}
