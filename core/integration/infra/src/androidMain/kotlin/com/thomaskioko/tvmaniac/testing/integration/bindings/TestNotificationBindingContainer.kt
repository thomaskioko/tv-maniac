package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager
import com.thomaskioko.tvmaniac.core.notifications.implementation.AndroidNotificationManager
import com.thomaskioko.tvmaniac.core.notifications.testing.FakeNotificationManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [
        AndroidNotificationManager::class,
        FakeNotificationManager::class,
    ],
)
public object TestNotificationBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideNotificationManager(): NotificationManager = FakeNotificationManager()
}
