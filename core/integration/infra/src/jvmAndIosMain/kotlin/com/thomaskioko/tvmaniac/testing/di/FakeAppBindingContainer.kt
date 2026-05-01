package com.thomaskioko.tvmaniac.testing.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.appconfig.ApplicationInfo
import com.thomaskioko.tvmaniac.appconfig.Platform
import com.thomaskioko.tvmaniac.core.base.ComputationCoroutineScope
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.base.MainCoroutineScope
import com.thomaskioko.tvmaniac.core.base.TmdbApi
import com.thomaskioko.tvmaniac.core.base.TraktApi
import com.thomaskioko.tvmaniac.core.base.di.BaseBindingContainer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.domain.library.LibrarySyncWorker
import com.thomaskioko.tvmaniac.domain.notifications.EpisodeNotificationWorker
import com.thomaskioko.tvmaniac.domain.upnext.UpNextSyncWorker
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetConfig
import com.thomaskioko.tvmaniac.genreshows.nav.GenreShowsRoute
import com.thomaskioko.tvmaniac.navigation.BaseRouteSerializer
import com.thomaskioko.tvmaniac.navigation.DefaultBaseRouteSerializer
import com.thomaskioko.tvmaniac.navigation.DefaultNavRootSerializer
import com.thomaskioko.tvmaniac.navigation.DefaultNavRouteSerializer
import com.thomaskioko.tvmaniac.navigation.DefaultNavigator
import com.thomaskioko.tvmaniac.navigation.DefaultSheetConfigSerializer
import com.thomaskioko.tvmaniac.navigation.DefaultSheetNavigator
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRootBinding
import com.thomaskioko.tvmaniac.navigation.NavRootSerializer
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import com.thomaskioko.tvmaniac.navigation.NavRouteSerializer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.navigation.SheetChild
import com.thomaskioko.tvmaniac.navigation.SheetChildFactory
import com.thomaskioko.tvmaniac.navigation.SheetConfig
import com.thomaskioko.tvmaniac.navigation.SheetConfigBinding
import com.thomaskioko.tvmaniac.navigation.SheetConfigSerializer
import com.thomaskioko.tvmaniac.navigation.SheetNavigator
import com.thomaskioko.tvmaniac.navigation.testing.FakeSheetNavigator
import com.thomaskioko.tvmaniac.presenter.root.DefaultRootPresenter
import com.thomaskioko.tvmaniac.presenter.root.RootPresenter
import com.thomaskioko.tvmaniac.traktauth.implementation.TokenRefreshWorker
import com.thomaskioko.tvmaniac.util.api.AppUtils
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

@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [
        BaseBindingContainer::class,
        EpisodeNotificationWorker::class,
        LibrarySyncWorker::class,
        TokenRefreshWorker::class,
        UpNextSyncWorker::class,
        DefaultSheetNavigator::class,
    ],
)
public object FakeAppBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideAppCoroutineDispatchers(): AppCoroutineDispatchers = AppCoroutineDispatchers(
        io = Dispatchers.Main.immediate,
        computation = Dispatchers.Main.immediate,
        databaseWrite = Dispatchers.Main.immediate,
        databaseRead = Dispatchers.Main.immediate,
        main = Dispatchers.Main.immediate,
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
    public fun provideAppUtils(): AppUtils = object : AppUtils {
        override fun isYoutubePlayerInstalled(): Flow<Boolean> =
            flowOf(false)
    }

    @Provides
    @SingleIn(AppScope::class)
    public fun provideSheetNavigator(): SheetNavigator = FakeSheetNavigator()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideNavDestinations(): Set<NavDestination> = setOf(
        object : NavDestination {
            override fun matches(route: NavRoute): Boolean = true
            override fun createChild(
                route: NavRoute,
                componentContext: ComponentContext,
            ): RootChild = object : RootChild {}
        },
    )

    @Provides
    @SingleIn(AppScope::class)
    public fun provideNavRouteBindings(): Set<NavRouteBinding<*>> = setOf(
        NavRouteBinding(GenreShowsRoute::class, GenreShowsRoute.serializer()),
    )

    @Provides
    @SingleIn(AppScope::class)
    public fun provideNavRouteSerializer(
        bindings: Set<NavRouteBinding<*>>,
    ): NavRouteSerializer = DefaultNavRouteSerializer(bindings)

    @Provides
    @SingleIn(AppScope::class)
    public fun provideSheetChildFactories(): Set<SheetChildFactory> = setOf(
        object : SheetChildFactory {
            override fun matches(config: SheetConfig): Boolean = config is EpisodeSheetConfig
            override fun createChild(
                config: SheetConfig,
                componentContext: ComponentContext,
            ): SheetChild = object : SheetChild {}
        },
    )

    @Provides
    @SingleIn(AppScope::class)
    public fun provideSheetConfigBindings(): Set<SheetConfigBinding<*>> = setOf(
        SheetConfigBinding(EpisodeSheetConfig::class, EpisodeSheetConfig.serializer()),
    )

    @Provides
    @SingleIn(AppScope::class)
    public fun provideSheetConfigSerializer(
        bindings: Set<SheetConfigBinding<*>>,
    ): SheetConfigSerializer = DefaultSheetConfigSerializer(bindings)

    @Provides
    @SingleIn(AppScope::class)
    public fun provideNavRoots(): Set<NavRoot> = setOf(
        com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot,
        com.thomaskioko.tvmaniac.library.nav.LibraryRoot,
        com.thomaskioko.tvmaniac.profile.nav.ProfileRoot,
        com.thomaskioko.tvmaniac.progress.nav.ProgressRoot,
    )

    @Provides
    @SingleIn(AppScope::class)
    public fun provideNavRootBindings(): Set<NavRootBinding<*>> = setOf(
        NavRootBinding(
            com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot::class,
            com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot.serializer(),
        ),
        NavRootBinding(
            com.thomaskioko.tvmaniac.library.nav.LibraryRoot::class,
            com.thomaskioko.tvmaniac.library.nav.LibraryRoot.serializer(),
        ),
        NavRootBinding(
            com.thomaskioko.tvmaniac.profile.nav.ProfileRoot::class,
            com.thomaskioko.tvmaniac.profile.nav.ProfileRoot.serializer(),
        ),
        NavRootBinding(
            com.thomaskioko.tvmaniac.progress.nav.ProgressRoot::class,
            com.thomaskioko.tvmaniac.progress.nav.ProgressRoot.serializer(),
        ),
    )

    @Provides
    @SingleIn(AppScope::class)
    public fun provideNavRootSerializer(
        bindings: Set<NavRootBinding<*>>,
    ): NavRootSerializer = DefaultNavRootSerializer(bindings)

    @Provides
    @SingleIn(AppScope::class)
    public fun provideBaseRouteSerializer(
        routeBindings: Set<NavRouteBinding<*>>,
        rootBindings: Set<NavRootBinding<*>>,
    ): BaseRouteSerializer = DefaultBaseRouteSerializer(routeBindings, rootBindings)

    @Provides
    @SingleIn(AppScope::class)
    public fun provideRootNavigator(
        navRouteSerializer: NavRouteSerializer,
        navRootSerializer: NavRootSerializer,
        baseRouteSerializer: BaseRouteSerializer,
        navRoots: Set<NavRoot>,
    ): Navigator = DefaultNavigator(
        navRouteSerializer = navRouteSerializer,
        navRootSerializer = navRootSerializer,
        baseRouteSerializer = baseRouteSerializer,
        navRoots = navRoots,
    )

    @Provides
    public fun provideRootPresenterFactory(
        factory: DefaultRootPresenter.Factory,
    ): RootPresenter.Factory = object : RootPresenter.Factory {
        override fun invoke(
            componentContext: ComponentContext,
        ): RootPresenter = factory.create(componentContext)
    }
}
