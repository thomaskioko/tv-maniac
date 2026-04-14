package com.thomaskioko.tvmaniac.app.debug

import com.thomaskioko.tvmaniac.app.R
import com.thomaskioko.tvmaniac.app.util.AppNotificationIconProvider
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationIconProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [AppNotificationIconProvider::class])
public class DebugNotificationIconProvider : NotificationIconProvider {
    override val smallIconResId: Int = R.drawable.ic_launcher_monochrome
    override val debugIconResId: Int = R.drawable.ic_debug_bug
}
