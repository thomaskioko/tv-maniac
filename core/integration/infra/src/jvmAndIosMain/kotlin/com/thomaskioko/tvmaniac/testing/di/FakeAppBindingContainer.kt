package com.thomaskioko.tvmaniac.testing.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.appconfig.AppMetadata
import com.thomaskioko.tvmaniac.appconfig.Platform
import com.thomaskioko.tvmaniac.core.base.ComputationCoroutineScope
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.base.IsDebugBuild
import com.thomaskioko.tvmaniac.core.base.MainCoroutineScope
import com.thomaskioko.tvmaniac.core.base.TmdbApi
import com.thomaskioko.tvmaniac.core.base.TraktApi
import com.thomaskioko.tvmaniac.core.base.di.BaseBindingContainer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.domain.episode.PendingUploadsWorker
import com.thomaskioko.tvmaniac.domain.library.LibrarySyncWorker
import com.thomaskioko.tvmaniac.domain.notifications.EpisodeNotificationWorker
import com.thomaskioko.tvmaniac.genreshows.nav.GenreShowsRoute
import com.thomaskioko.tvmaniac.library.nav.LibraryRoot
import com.thomaskioko.tvmaniac.navigation.BaseRouteSerializer
import com.thomaskioko.tvmaniac.navigation.DefaultBaseRouteSerializer
import com.thomaskioko.tvmaniac.navigation.DefaultNavRootSerializer
import com.thomaskioko.tvmaniac.navigation.DefaultNavRouteSerializer
import com.thomaskioko.tvmaniac.navigation.DefaultNavigator
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRootBinding
import com.thomaskioko.tvmaniac.navigation.NavRootSerializer
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import com.thomaskioko.tvmaniac.navigation.NavRouteSerializer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.OverlayRoute
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.presenter.root.DefaultRootPresenter
import com.thomaskioko.tvmaniac.presenter.root.RootPresenter
import com.thomaskioko.tvmaniac.profile.nav.ProfileRoot
import com.thomaskioko.tvmaniac.progress.nav.ProgressRoot
import com.thomaskioko.tvmaniac.traktauth.implementation.TokenRefreshWorker
import com.thomaskioko.tvmaniac.util.api.AppUtils
import com.thomaskioko.tvmaniac.watchlist.nav.WatchlistRoot
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
        PendingUploadsWorker::class,
        TokenRefreshWorker::class,
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
    public fun provideAppMetadata(): AppMetadata =
        AppMetadata(
            versionName = "0.0.1",
            versionCode = 1,
            packageName = "com.thomaskioko.tvmaniac.test",
            platform = Platform.ANDROID,
        )

    @Provides
    @IsDebugBuild
    public fun provideIsDebugBuild(): Boolean = true

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
    public fun provideNavDestinations(): Set<NavDestination<*>> = setOf(
        NavDestination.Screen(
            routeClass = NavRoute::class,
        ) { _: NavRoute, _: ComponentContext -> object : RootChild {} },
        NavDestination.Overlay(
            routeClass = OverlayRoute::class,
        ) { _: NavRoute, _: ComponentContext -> object : RootChild {} },
        NavDestination.TabRoot(
            routeClass = DiscoverRoot::class,
        ) { _: DiscoverRoot, _: ComponentContext -> object : RootChild {} },
        NavDestination.TabRoot(
            routeClass = LibraryRoot::class,
        ) { _: LibraryRoot, _: ComponentContext -> object : RootChild {} },
        NavDestination.TabRoot(
            routeClass = ProfileRoot::class,
        ) { _: ProfileRoot, _: ComponentContext -> object : RootChild {} },
        NavDestination.TabRoot(
            routeClass = ProgressRoot::class,
        ) { _: ProgressRoot, _: ComponentContext -> object : RootChild {} },
        NavDestination.TabRoot(
            routeClass = WatchlistRoot::class,
        ) { _: WatchlistRoot, _: ComponentContext -> object : RootChild {} },
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
    public fun provideNavRoots(): Set<NavRoot> = setOf(
        DiscoverRoot,
        LibraryRoot,
        ProfileRoot,
        ProgressRoot,
        WatchlistRoot,
    )

    @Provides
    @SingleIn(AppScope::class)
    public fun provideNavRootBindings(): Set<NavRootBinding<*>> = setOf(
        NavRootBinding(
            DiscoverRoot::class,
            DiscoverRoot.serializer(),
        ),
        NavRootBinding(
            LibraryRoot::class,
            LibraryRoot.serializer(),
        ),
        NavRootBinding(
            ProfileRoot::class,
            ProfileRoot.serializer(),
        ),
        NavRootBinding(
            ProgressRoot::class,
            ProgressRoot.serializer(),
        ),
        NavRootBinding(
            WatchlistRoot::class,
            WatchlistRoot.serializer(),
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
        navRoots: Set<NavRoot>,
    ): BaseRouteSerializer = DefaultBaseRouteSerializer(routeBindings, rootBindings, navRoots)

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
