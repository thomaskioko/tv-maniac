package com.thomaskioko.tvmaniac.app.util

import com.thomaskioko.tvmaniac.app.R
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationIconProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AppNotificationIconProvider : NotificationIconProvider {
    override val smallIconResId: Int = R.drawable.ic_launcher_monochrome
}
