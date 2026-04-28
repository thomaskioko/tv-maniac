package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.core.tasks.implementation.AndroidTaskScheduler
import com.thomaskioko.tvmaniac.core.tasks.testing.FakeBackgroundTaskScheduler
import com.thomaskioko.tvmaniac.domain.library.LibrarySyncWorker
import com.thomaskioko.tvmaniac.domain.notifications.EpisodeNotificationWorker
import com.thomaskioko.tvmaniac.domain.upnext.UpNextSyncWorker
import com.thomaskioko.tvmaniac.traktauth.implementation.TokenRefreshWorker
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [
        TokenRefreshWorker::class,
        EpisodeNotificationWorker::class,
        LibrarySyncWorker::class,
        UpNextSyncWorker::class,
        AndroidTaskScheduler::class,
    ],
)
public object TestWorkerBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideBackgroundTaskScheduler(): BackgroundTaskScheduler = FakeBackgroundTaskScheduler()
}
