package com.thomaskioko.tvmaniac.inject

import com.thomaskioko.tvmaniac.app.R
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationIconProvider
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AppNotificationIconProvider : NotificationIconProvider {
    override val smallIconResId: Int = R.drawable.ic_launcher_monochrome
}
