package com.thomaskioko.tvmaniac.presenter.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.extensions.asStateFlow
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.componentCoroutineScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.domain.user.ObserveUserProfileInteractor
import com.thomaskioko.tvmaniac.presentation.library.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.progress.ProgressPresenter
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable

@Serializable
public data class ProfileAvatar(val url: String? = null)

@AssistedInject
public class HomePresenter(
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
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val navigation = StackNavigation<HomeConfig>()

    private val homeChildStackRouter: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        key = "HomeChildStackKey",
        initialConfiguration = HomeConfig.Discover,
        serializer = HomeConfig.serializer(),
        handleBackButton = true,
        childFactory = ::child,
    )

    public val homeChildStack: StateFlow<ChildStack<*, Child>> =
        homeChildStackRouter.asStateFlow(componentContext.componentCoroutineScope())

    public val homeChildStackValue: Value<ChildStack<*, Child>> = homeChildStackRouter

    public val profileAvatarUrl: StateFlow<String?> = run {
        observeUserProfileInteractor(Unit)
        observeUserProfileInteractor.flow
            .map { it?.avatarUrl }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )
    }

    public val profileAvatarUrlValue: Value<ProfileAvatar> =
        profileAvatarUrl
            .map { ProfileAvatar(url = it) }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProfileAvatar(),
            )
            .asValue(coroutineScope)

    public fun onDiscoverClicked() {
        onTabClicked(HomeConfig.Discover)
    }

    public fun onProgressClicked() {
        onTabClicked(HomeConfig.Progress)
    }

    public fun onLibraryClicked() {
        onTabClicked(HomeConfig.Library)
    }

    public fun onProfileClicked() {
        onTabClicked(HomeConfig.Profile)
    }

    public fun onTabClicked(config: HomeConfig) {
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
                        onNavigateToEpisode = { _, episodeId -> onDiscoverEpisodeLongPressed(episodeId) },
                        onNavigateToSeason = { _, _, _ -> },
                        onNavigateToUpNext = { onProgressClicked() },
                        onNavigateToSearch = onNavigateToSearch,
                    ),
                )
            }

            HomeConfig.Progress -> {
                Child.Progress(
                    presenter = progressPresenterFactory.create(
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
                    presenter = libraryPresenterFactory.create(
                        componentContext = componentContext,
                        navigateToShowDetails = { id ->
                            onShowClicked(id)
                        },
                    ),
                )
            }

            HomeConfig.Profile -> {
                Child.Profile(
                    presenter = profilePresenterFactory.create(
                        componentContext = componentContext,
                        onSettings = onSettingsClicked,
                    ),
                )
            }
        }

    public sealed interface Child {
        public class Discover(public val presenter: DiscoverShowsPresenter) : Child

        public class Progress(public val presenter: ProgressPresenter) : Child

        public class Library(public val presenter: LibraryPresenter) : Child

        public class Profile(public val presenter: ProfilePresenter) : Child
    }

    @Serializable
    public sealed interface HomeConfig {
        @Serializable
        public data object Discover : HomeConfig

        @Serializable
        public data object Progress : HomeConfig

        @Serializable
        public data object Library : HomeConfig

        @Serializable
        public data object Profile : HomeConfig
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(
            componentContext: ComponentContext,
            onShowClicked: (id: Long) -> Unit,
            onMoreShowClicked: (id: Long) -> Unit,
            onShowGenreClicked: (id: Long) -> Unit,
            onNavigateToSearch: () -> Unit,
            onSettingsClicked: () -> Unit,
            onSeasonClicked: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
            onDiscoverEpisodeLongPressed: (Long) -> Unit,
            onUpNextEpisodeLongPressed: (Long) -> Unit,
            onCalendarEpisodeLongPressed: (Long) -> Unit,
        ): HomePresenter
    }
}
