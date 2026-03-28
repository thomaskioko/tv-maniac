package com.thomaskioko.tvmaniac.presenter.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.childStack
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asStateFlow
import com.thomaskioko.tvmaniac.core.base.extensions.componentCoroutineScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.domain.user.ObserveUserProfileInteractor
import com.thomaskioko.tvmaniac.presentation.library.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.progress.ProgressPresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter.Child
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter.HomeConfig
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    @Assisted private val onNavigateToSearch: () -> Unit,
    @Assisted private val onSettingsClicked: () -> Unit,
    @Assisted private val onSeasonClicked: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    @Assisted private val onDiscoverEpisodeLongPressed: (Long) -> Unit,
    @Assisted private val onUpNextEpisodeLongPressed: (Long) -> Unit,
    @Assisted private val onCalendarEpisodeLongPressed: (Long) -> Unit,
    private val discoverPresenterFactory: DiscoverShowsPresenter.Factory,
    private val progressPresenterFactory: ProgressPresenter.Factory,
    private val libraryPresenterFactory: LibraryPresenter.Factory,
    private val profilePresenterFactory: ProfilePresenter.Factory,
    private val observeUserProfileInteractor: ObserveUserProfileInteractor,
) : ComponentContext by componentContext, HomePresenter {
    private val navigation = StackNavigation<HomeConfig>()
    private val coroutineScope: CoroutineScope = componentContext.coroutineScope()

    override val homeChildStack: StateFlow<ChildStack<*, Child>> = childStack(
        source = navigation,
        key = "HomeChildStackKey",
        initialConfiguration = HomeConfig.Discover,
        serializer = HomeConfig.serializer(),
        handleBackButton = true,
        childFactory = ::child,
    ).asStateFlow(componentContext.componentCoroutineScope())

    override val profileAvatarUrl: StateFlow<String?> = run {
        observeUserProfileInteractor(Unit)
        observeUserProfileInteractor.flow
            .map { it?.avatarUrl }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )
    }

    override fun onDiscoverClicked() {
        onTabClicked(HomeConfig.Discover)
    }

    override fun onProgressClicked() {
        onTabClicked(HomeConfig.Progress)
    }

    override fun onLibraryClicked() {
        onTabClicked(HomeConfig.Library)
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
                        onNavigateToEpisode = { _, episodeId -> onDiscoverEpisodeLongPressed(episodeId) },
                        onNavigateToUpNext = { onProgressClicked() },
                        onNavigateToSearch = onNavigateToSearch,
                    ),
                )
            }

            HomeConfig.Progress -> {
                Child.Progress(
                    presenter = progressPresenterFactory(
                        componentContext = componentContext,
                        navigateToShowDetails = { id -> onShowClicked(id) },
                        navigateToSeasonDetails = onSeasonClicked,
                        onUpNextEpisodeLongPressed = onUpNextEpisodeLongPressed,
                        onCalendarEpisodeLongPressed = onCalendarEpisodeLongPressed,
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
        private val progressPresenterFactory: ProgressPresenter.Factory,
        private val libraryPresenterFactory: LibraryPresenter.Factory,
        private val profilePresenterFactory: ProfilePresenter.Factory,
        private val observeUserProfileInteractor: ObserveUserProfileInteractor,
    ) : HomePresenter.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            onShowClicked: (id: Long) -> Unit,
            onMoreShowClicked: (id: Long) -> Unit,
            onShowGenreClicked: (id: Long) -> Unit,
            onNavigateToSearch: () -> Unit,
            onSettingsClicked: () -> Unit,
            onSeasonClicked: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
            onDiscoverEpisodeLongPressed: (episodeId: Long) -> Unit,
            onUpNextEpisodeLongPressed: (episodeId: Long) -> Unit,
            onCalendarEpisodeLongPressed: (episodeId: Long) -> Unit,
        ): HomePresenter = DefaultHomePresenter(
            componentContext = componentContext,
            onShowClicked = onShowClicked,
            onMoreShowClicked = onMoreShowClicked,
            onShowGenreClicked = onShowGenreClicked,
            onNavigateToSearch = onNavigateToSearch,
            onSettingsClicked = onSettingsClicked,
            onSeasonClicked = onSeasonClicked,
            onDiscoverEpisodeLongPressed = onDiscoverEpisodeLongPressed,
            onUpNextEpisodeLongPressed = onUpNextEpisodeLongPressed,
            onCalendarEpisodeLongPressed = onCalendarEpisodeLongPressed,
            discoverPresenterFactory = discoverPresenterFactory,
            progressPresenterFactory = progressPresenterFactory,
            libraryPresenterFactory = libraryPresenterFactory,
            profilePresenterFactory = profilePresenterFactory,
            observeUserProfileInteractor = observeUserProfileInteractor,
        )
    }
}
