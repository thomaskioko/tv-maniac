package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.trakt.service.implementation.TraktPlatformBindingContainer
import com.thomaskioko.tvmaniac.appconfig.IosAppConfigBindingContainer
import com.thomaskioko.tvmaniac.core.logger.IosCrashReporter
import com.thomaskioko.tvmaniac.core.logger.IosCrashReporterBindingContainer
import com.thomaskioko.tvmaniac.core.notifications.implementation.IosNotificationManager
import com.thomaskioko.tvmaniac.core.tasks.implementation.IosTaskScheduler
import com.thomaskioko.tvmaniac.tmdb.implementation.TmdbPlatformBindingContainer
import com.thomaskioko.tvmaniac.traktauth.implementation.DefaultIOSTraktAuthManager
import com.thomaskioko.tvmaniac.util.IosAppUtils
import com.thomaskioko.tvmaniac.util.IosFormatterUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo

@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [
        IosAppConfigBindingContainer::class,
        TmdbPlatformBindingContainer::class,
        TraktPlatformBindingContainer::class,
        IosFormatterUtil::class,
        IosAppUtils::class,
        DefaultIOSTraktAuthManager::class,
        IosNotificationManager::class,
        IosCrashReporter::class,
        IosCrashReporterBindingContainer::class,
        IosTaskScheduler::class,
    ],
)
internal object FakeIosPlatformBindingContainer
