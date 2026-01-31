package com.thomaskioko.tvmaniac.presenter.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.childStack
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asStateFlow
import com.thomaskioko.tvmaniac.core.base.extensions.componentCoroutineScope
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presentation.library.LibraryPresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter.Child
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter.HomeConfig
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(ActivityScope::class)
public class DefaultHomePresenter private constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onShowClicked: (id: Long) -> Unit,
    @Assisted private val onMoreShowClicked: (id: Long) -> Unit,
    @Assisted private val onShowGenreClicked: (id: Long) -> Unit,
    @Assisted private val onSettingsClicked: () -> Unit,
    private val discoverPresenterFactory: DiscoverShowsPresenter.Factory,
    private val libraryPresenterFactory: LibraryPresenter.Factory,
    private val searchPresenterFactory: SearchShowsPresenter.Factory,
    private val profilePresenterFactory: ProfilePresenter.Factory,
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

    override fun onProfileClicked() {
        onTabClicked(HomeConfig.Profile)
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
                    presenter = discoverPresenterFactory(
                        componentContext = componentContext,
                        onNavigateToShowDetails = { id -> onShowClicked(id) },
                        onNavigateToMore = { id -> onMoreShowClicked(id) },
                        onNavigateToEpisode = { showId, episodeId ->
                            // TODO:: Add Navigation to episode detail
                        },
                    ),
                )
            }

            HomeConfig.Library -> {
                Child.Library(
                    presenter = libraryPresenterFactory(
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

            HomeConfig.Profile -> {
                Child.Profile(
                    presenter = profilePresenterFactory(
                        componentContext = componentContext,
                        navigateToSettings = onSettingsClicked,
                    ),
                )
            }
        }

    @Inject
    @SingleIn(ActivityScope::class)
    @ContributesBinding(ActivityScope::class, HomePresenter.Factory::class)
    public class Factory(
        private val discoverPresenterFactory: DiscoverShowsPresenter.Factory,
        private val libraryPresenterFactory: LibraryPresenter.Factory,
        private val searchPresenterFactory: SearchShowsPresenter.Factory,
        private val profilePresenterFactory: ProfilePresenter.Factory,
    ) : HomePresenter.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            onShowClicked: (id: Long) -> Unit,
            onMoreShowClicked: (id: Long) -> Unit,
            onShowGenreClicked: (id: Long) -> Unit,
            onNavigateToProfile: () -> Unit,
            onSettingsClicked: () -> Unit,
        ): HomePresenter = DefaultHomePresenter(
            componentContext = componentContext,
            onShowClicked = onShowClicked,
            onMoreShowClicked = onMoreShowClicked,
            onShowGenreClicked = onShowGenreClicked,
            onSettingsClicked = onSettingsClicked,
            discoverPresenterFactory = discoverPresenterFactory,
            libraryPresenterFactory = libraryPresenterFactory,
            searchPresenterFactory = searchPresenterFactory,
            profilePresenterFactory = profilePresenterFactory,
        )
    }
}
