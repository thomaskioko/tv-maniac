package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.core.logger.CrashReporter
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeCrashReporter
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager
import com.thomaskioko.tvmaniac.core.notifications.testing.FakeNotificationManager
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.core.tasks.testing.FakeBackgroundTaskScheduler
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
public object TestPlatformFakesBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideCrashReporter(): CrashReporter = FakeCrashReporter()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideNotificationManager(): NotificationManager = FakeNotificationManager()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideBackgroundTaskScheduler(): BackgroundTaskScheduler = FakeBackgroundTaskScheduler()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideFormatterUtil(): FormatterUtil = FakeFormatterUtil()
}
