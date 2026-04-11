package com.thomaskioko.tvmaniac.testing.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ComputationCoroutineScope
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.base.MainCoroutineScope
import com.thomaskioko.tvmaniac.core.base.TmdbApi
import com.thomaskioko.tvmaniac.core.base.TraktApi
import com.thomaskioko.tvmaniac.core.base.di.BaseBindingContainer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.CompositeLogger
import com.thomaskioko.tvmaniac.core.logger.CrashReporter
import com.thomaskioko.tvmaniac.core.logger.FirebaseCrashLogger
import com.thomaskioko.tvmaniac.core.logger.KermitLogger
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeCrashReporter
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.core.tasks.testing.FakeBackgroundTaskScheduler
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.data.user.implementation.DefaultUserRepository
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.implementation.DefaultDatastoreRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.library.LibrarySyncWorker
import com.thomaskioko.tvmaniac.domain.notifications.EpisodeNotificationWorker
import com.thomaskioko.tvmaniac.domain.upnext.UpNextSyncWorker
import com.thomaskioko.tvmaniac.locale.api.LocaleProvider
import com.thomaskioko.tvmaniac.locale.testing.FakeLocaleProvider
import com.thomaskioko.tvmaniac.navigation.DefaultRootPresenter
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.implementation.DefaultRequestManagerRepository
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.syncactivity.implementation.DefaultTraktActivityRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.implementation.DefaultTraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.implementation.TokenRefreshWorker
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import com.thomaskioko.tvmaniac.traktlists.implementation.DefaultTraktListRepository
import com.thomaskioko.tvmaniac.traktlists.testing.FakeTraktListRepository
import com.thomaskioko.tvmaniac.util.api.AppUtils
import com.thomaskioko.tvmaniac.util.api.ApplicationInfo
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import com.thomaskioko.tvmaniac.util.api.Platform
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Binding container of fake / mock providers used by tests. Contributed to
 * [AppScope] and replaces the production bindings it stands in for via
 * [ContributesTo.replaces], so `TestJvmGraph` / `TestIosGraph` pick these up
 * automatically when they merge the scope.
 */
@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [
        BaseBindingContainer::class,
        DefaultDatastoreRepository::class,
        DefaultUserRepository::class,
        DefaultRequestManagerRepository::class,
        DefaultTraktActivityRepository::class,
        DefaultTraktAuthRepository::class,
        DefaultTraktListRepository::class,
        CompositeLogger::class,
        KermitLogger::class,
        FirebaseCrashLogger::class,
        EpisodeNotificationWorker::class,
        LibrarySyncWorker::class,
        TokenRefreshWorker::class,
        UpNextSyncWorker::class,
    ],
)
public object FakeAppBindings {
    @Provides
    @SingleIn(AppScope::class)
    public fun provideDatastoreRepository(): DatastoreRepository = FakeDatastoreRepository()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideTraktAuthManager(): TraktAuthManager = FakeTraktAuthManager()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideTraktAuthRepository(): TraktAuthRepository = FakeTraktAuthRepository()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideLogger(): Logger = FakeLogger()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideUserRepository(): UserRepository = FakeUserRepository()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideTraktListRepository(): TraktListRepository = FakeTraktListRepository()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideRequestManagerRepository(): RequestManagerRepository = FakeRequestManagerRepository()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideTraktActivityRepository(): TraktActivityRepository =
        FakeTraktActivityRepository()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideAppCoroutineDispatchers(): AppCoroutineDispatchers = AppCoroutineDispatchers(
        io = Dispatchers.Default,
        computation = Dispatchers.Default,
        databaseWrite = Dispatchers.Default,
        databaseRead = Dispatchers.Default,
        main = Dispatchers.Default,
    )

    @Provides
    @IoCoroutineScope
    @SingleIn(AppScope::class)
    public fun provideIoCoroutineScope(dispatchers: AppCoroutineDispatchers): CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatchers.io)

    @Provides
    @MainCoroutineScope
    @SingleIn(AppScope::class)
    public fun provideMainCoroutineScope(dispatchers: AppCoroutineDispatchers): CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatchers.main)

    @Provides
    @ComputationCoroutineScope
    @SingleIn(AppScope::class)
    public fun provideComputationCoroutineScope(dispatchers: AppCoroutineDispatchers): CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatchers.computation)

    @Provides
    public fun provideCoroutineScope(@MainCoroutineScope scope: CoroutineScope): CoroutineScope =
        scope

    @Provides
    @SingleIn(AppScope::class)
    public fun provideApplicationInfo(): ApplicationInfo =
        ApplicationInfo(
            debugBuild = true,
            versionName = "0.0.1",
            versionCode = 1,
            packageName = "com.thomaskioko.tvmaniac.test",
            platform = Platform.ANDROID,
        )

    @Provides
    @SingleIn(AppScope::class)
    @TmdbApi
    public fun provideTmdbHttpClientEngine(): io.ktor.client.engine.HttpClientEngine =
        MockEngine { respond("{}", HttpStatusCode.OK) }

    @Provides
    @SingleIn(AppScope::class)
    @TraktApi
    public fun provideTraktHttpClientEngine(): io.ktor.client.engine.HttpClientEngine =
        MockEngine { respond("{}", HttpStatusCode.OK) }

    @Provides
    @SingleIn(AppScope::class)
    public fun provideNotificationManager(): NotificationManager =
        com.thomaskioko.tvmaniac.core.notifications.testing.FakeNotificationManager()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideLocaleProvider(): LocaleProvider = FakeLocaleProvider()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideCrashReporter(): CrashReporter = FakeCrashReporter()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideBackgroundTaskScheduler(): BackgroundTaskScheduler = FakeBackgroundTaskScheduler()

    @Provides
    public fun provideAppUtils(): AppUtils = object : AppUtils {
        override fun isYoutubePlayerInstalled(): Flow<Boolean> =
            flowOf(false)
    }

    @Provides
    public fun provideFormatterUtil(): FormatterUtil =
        FakeFormatterUtil()

    @Provides
    public fun provideRootPresenterFactory(
        factory: DefaultRootPresenter.Factory,
    ): RootPresenter.Factory = object : RootPresenter.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            navigator: RootNavigator,
        ): RootPresenter = factory.create(componentContext, navigator)
    }
}
