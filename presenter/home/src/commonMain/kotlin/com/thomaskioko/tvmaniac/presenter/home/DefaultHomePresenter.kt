package com.thomaskioko.tvmaniac.presenter.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.childStack
import com.thomaskioko.tvmaniac.core.base.extensions.asStateFlow
import com.thomaskioko.tvmaniac.core.base.extensions.componentCoroutineScope
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter.Child
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter.HomeConfig
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.StateFlow

@Inject
class DefaultHomePresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted("toShowDetail") val onShowClicked: (id: Long) -> Unit,
    @Assisted("toMoreShows") val onMoreShowClicked: (id: Long) -> Unit,
    @Assisted("toGenres") val onShowGenreClicked: (id: Long) -> Unit,
    private val discoverPresenterFactory: DiscoverShowsPresenter.Factory,
    private val watchlistPresenterFactory: WatchlistPresenter.Factory,
    private val searchPresenterFactory: SearchShowsPresenter.Factory,
    private val settingsPresenterFactory: SettingsPresenter.Factory,
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

    private inline fun <C : Any> StackNavigator<C>.switchTab(
        configuration: C,
        crossinline onComplete: () -> Unit = {},
    ) {
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
                        componentContext = componentContext,
                        onNavigateToShowDetails = { id -> onShowClicked(id) },
                        onNavigateToMore = { id -> onMoreShowClicked(id) },
                    ),
                )
            }

            HomeConfig.Library -> {
                Child.Watchlist(
                    presenter = watchlistPresenterFactory(
                        componentContext = componentContext,
                        navigateToShowDetails = { id ->
                            onShowClicked(id)
                        },
                    ),
                )
            }

            HomeConfig.Search -> {
                Child.Search(
                    presenter = searchPresenterFactory(
                        componentContext = componentContext,
                        onNavigateToShowDetails = { id -> onShowClicked(id) },
                        onNavigateToGenre = { id -> onShowGenreClicked(id) },
                    ),
                )
            }

            HomeConfig.Settings -> {
                Child.Settings(
                    presenter = settingsPresenterFactory.create(
                        componentContext = componentContext,
                        launchWebView = {
                            traktAuthManager.launchWebView()
                        },
                    ),
                )
            }
        }

    @AssistedFactory
    fun interface Factory {
        fun create(
            @Assisted componentContext: ComponentContext,
            @Assisted("toShowDetail") onShowClicked: (id: Long) -> Unit,
            @Assisted("toMoreShows") onMoreShowClicked: (id: Long) -> Unit,
            @Assisted("toGenres") onShowGenreClicked: (id: Long) -> Unit,
        ): DefaultHomePresenter
    }
}
