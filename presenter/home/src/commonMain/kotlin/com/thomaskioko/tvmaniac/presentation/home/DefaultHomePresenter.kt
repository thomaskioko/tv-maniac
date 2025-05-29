package com.thomaskioko.tvmaniac.presentation.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.childStack
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asStateFlow
import com.thomaskioko.tvmaniac.core.base.extensions.componentCoroutineScope
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverPresenterFactory
import com.thomaskioko.tvmaniac.presentation.home.HomePresenter.Child
import com.thomaskioko.tvmaniac.presentation.home.HomePresenter.HomeConfig
import com.thomaskioko.tvmaniac.presentation.search.SearchPresenterFactory
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistPresenterFactory
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(ActivityScope::class)
class DefaultHomePresenter private constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onShowClicked: (id: Long) -> Unit,
    @Assisted private val onMoreShowClicked: (id: Long) -> Unit,
    @Assisted private val onShowGenreClicked: (id: Long) -> Unit,
    private val discoverPresenterFactory: DiscoverPresenterFactory,
    private val watchlistPresenterFactory: WatchlistPresenterFactory,
    private val searchPresenterFactory: SearchPresenterFactory,
    private val settingsPresenterFactory: SettingsPresenterFactory,
    private val traktAuthManager: TraktAuthManager,
) : ComponentContext by componentContext, HomePresenter {
    private val navigation = StackNavigation<HomeConfig>()

    override val homeChildStack: StateFlow<ChildStack<*, Child>> = childStack(
        source = navigation,
        key = "HomeChildStackKey",
        initialConfiguration = HomeConfig.Discover,
        serializer = HomeConfig.serializer(),
        handleBackButton = true,
        childFactory = ::child,
    ).asStateFlow(componentContext.componentCoroutineScope())

    override fun onDiscoverClicked() {
        onTabClicked(HomeConfig.Discover)
    }

    override fun onLibraryClicked() {
        onTabClicked(HomeConfig.Library)
    }

    override fun onSearchClicked() {
        onTabClicked(HomeConfig.Search)
    }

    override fun onSettingsClicked() {
        onTabClicked(HomeConfig.Settings)
    }

    override fun onTabClicked(config: HomeConfig) {
        navigation.switchTab(config)
    }

    private inline fun <C : Any> StackNavigator<C>.switchTab(configuration: C, crossinline onComplete: () -> Unit = {}) {
        navigate(
            transformer = { stack ->
                val existing = stack.find { it::class == configuration::class }
                if (existing != null) {
                    stack.filterNot { it::class == configuration::class } + existing
                } else {
                    stack + configuration
                }
            },
            onComplete = { _, _ -> onComplete() },
        )
    }

    private fun child(config: HomeConfig, componentContext: ComponentContext): Child =
        when (config) {
            is HomeConfig.Discover -> {
                Child.Discover(
                    presenter = discoverPresenterFactory.create(
                        componentContext,
                        { id -> onShowClicked(id) },
                        { id -> onMoreShowClicked(id) },
                    ),
                )
            }
            HomeConfig.Library -> {
                Child.Watchlist(
                    presenter = watchlistPresenterFactory.create(
                        componentContext,
                    ) { id ->
                        onShowClicked(id)
                    },
                )
            }
            HomeConfig.Search -> {
                Child.Search(
                    presenter = searchPresenterFactory.create(
                        componentContext,
                        { id -> onShowClicked(id) },
                        { id -> onShowGenreClicked(id) },
                    ),
                )
            }
            HomeConfig.Settings -> {
                Child.Settings(
                    presenter = settingsPresenterFactory.create(
                        componentContext,
                    ) {
                        traktAuthManager.launchWebView()
                    },
                )
            }
        }

    @Inject
    @SingleIn(ActivityScope::class)
    @ContributesBinding(ActivityScope::class, HomePresenter.Factory::class)
    class Factory(
        private val discoverPresenterFactory: DiscoverPresenterFactory,
        private val watchlistPresenterFactory: WatchlistPresenterFactory,
        private val searchPresenterFactory: SearchPresenterFactory,
        private val settingsPresenterFactory: SettingsPresenterFactory,
        private val traktAuthManager: TraktAuthManager,
    ) : HomePresenter.Factory {
        override fun create(
            componentContext: ComponentContext,
            onShowClicked: (id: Long) -> Unit,
            onMoreShowClicked: (id: Long) -> Unit,
            onShowGenreClicked: (id: Long) -> Unit,
        ): HomePresenter = DefaultHomePresenter(
            componentContext = componentContext,
            onShowClicked = onShowClicked,
            onMoreShowClicked = onMoreShowClicked,
            onShowGenreClicked = onShowGenreClicked,
            discoverPresenterFactory = discoverPresenterFactory,
            watchlistPresenterFactory = watchlistPresenterFactory,
            searchPresenterFactory = searchPresenterFactory,
            settingsPresenterFactory = settingsPresenterFactory,
            traktAuthManager = traktAuthManager,
        )
    }
}
