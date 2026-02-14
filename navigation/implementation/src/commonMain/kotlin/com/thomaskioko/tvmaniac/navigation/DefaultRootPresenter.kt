package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asStateFlow
import com.thomaskioko.tvmaniac.core.base.extensions.componentCoroutineScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.view.InvokeError
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.debug.presenter.DebugPresenter
import com.thomaskioko.tvmaniac.domain.logout.LogoutInteractor
import com.thomaskioko.tvmaniac.domain.user.UpdateUserProfileData
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration.Companion.milliseconds

@OptIn(kotlinx.coroutines.FlowPreview::class)
@Inject
public class DefaultRootPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigator: RootNavigator,
    private val homePresenterFactory: HomePresenter.Factory,
    private val profilePresenterFactory: ProfilePresenter.Factory,
    private val settingsPresenterFactory: SettingsPresenter.Factory,
    private val debugPresenterFactory: DebugPresenter.Factory,
    private val moreShowsPresenterFactory: MoreShowsPresenter.Factory,
    private val showDetailsPresenterFactory: ShowDetailsPresenter.Factory,
    private val seasonDetailsPresenterFactory: SeasonDetailsPresenter.Factory,
    private val trailersPresenterFactory: TrailersPresenter.Factory,
    private val traktAuthRepository: TraktAuthRepository,
    private val updateUserProfileData: UpdateUserProfileData,
    private val logoutInteractor: LogoutInteractor,
    private val coroutineScope: CoroutineScope = componentContext.coroutineScope(),
    private val datastoreRepository: DatastoreRepository,
) : RootPresenter, ComponentContext by componentContext {

    init {
        coroutineScope.launch {
            datastoreRepository.setShowNotificationRationale(false)
        }

        coroutineScope.launch {
            traktAuthRepository.state
                .debounce(200.milliseconds)
                .distinctUntilChanged()
                .filter { it == TraktAuthState.LOGGED_IN }
                .collect { refreshUserProfile() }
        }

        coroutineScope.launch {
            traktAuthRepository.state
                .debounce(500.milliseconds)
                .distinctUntilChanged()
                .filter { it == TraktAuthState.LOGGED_IN }
                .take(1)
                .collect { showRationaleIfNeeded() }
        }
    }

    private suspend fun refreshUserProfile() {
        updateUserProfileData(UpdateUserProfileData.Params(forceRefresh = false))
            .collect { status ->
                if (status is InvokeError) {
                    if (status.throwable.message?.contains("401") == true) {
                        traktAuthRepository.setAuthError(AuthError.TokenExpired)
                        logoutInteractor.executeSync(Unit)
                    }
                }
            }
    }

    override val childStack: StateFlow<ChildStack<*, Child>> = childStack(
        source = navigator.getStackNavigation(),
        key = "RootChildStackKey",
        initialConfiguration = RootDestinationConfig.Home,
        serializer = RootDestinationConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createScreen,
    ).asStateFlow(componentContext.componentCoroutineScope())

    override val themeState: StateFlow<ThemeState> =
        datastoreRepository
            .observeTheme()
            .map { theme -> ThemeState(isFetching = false, appTheme = theme) }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ThemeState(),
            )

    override val notificationPermissionState: StateFlow<NotificationPermissionState> =
        combine(
            datastoreRepository.observeShowNotificationRationale(),
            datastoreRepository.observeRequestNotificationPermission(),
        ) { showRationale, requestPermission ->
            NotificationPermissionState(
                showRationale = showRationale,
                requestPermission = requestPermission,
            )
        }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = NotificationPermissionState(),
            )

    override fun onShowFollowed() {
        coroutineScope.launch {
            showRationaleIfNeeded()
        }
    }

    private suspend fun showRationaleIfNeeded() {
        combine(
            datastoreRepository.observeNotificationPermissionAsked(),
            datastoreRepository.observeShowNotificationRationale(),
        ) { hasAsked, isRationaleShowing ->
            !hasAsked && !isRationaleShowing
        }
            .filter { it }
            .take(1)
            .collect { datastoreRepository.setShowNotificationRationale(true) }
    }

    override fun onRationaleAccepted() {
        coroutineScope.launch {
            datastoreRepository.setShowNotificationRationale(false)
            datastoreRepository.setRequestNotificationPermission(true)
        }
    }

    override fun onRationaleDismissed() {
        coroutineScope.launch {
            datastoreRepository.setShowNotificationRationale(false)
        }
    }

    override fun onNotificationPermissionResult(granted: Boolean) {
        coroutineScope.launch {
            datastoreRepository.setRequestNotificationPermission(false)
            datastoreRepository.setNotificationPermissionAsked(true)
            if (granted) {
                datastoreRepository.setEpisodeNotificationsEnabled(true)
            }
        }
    }

    override fun onDeepLink(destination: DeepLinkDestination) {
        when (destination) {
            is DeepLinkDestination.ShowDetails -> {
                navigator.pushNew(
                    RootDestinationConfig.ShowDetails(
                        param = ShowDetailsParam(
                            id = destination.showId,
                            forceRefresh = destination.forceRefresh,
                        ),
                    ),
                )
            }
            is DeepLinkDestination.SeasonDetails -> {
                navigator.pushNew(
                    RootDestinationConfig.SeasonDetails(
                        param = SeasonDetailsUiParam(
                            showTraktId = destination.showId,
                            seasonNumber = destination.seasonNumber,
                            seasonId = destination.seasonId,
                            forceRefresh = destination.forceRefresh,
                        ),
                    ),
                )
            }
            is DeepLinkDestination.DebugMenu -> {
                navigator.pushNew(RootDestinationConfig.Debug)
            }
        }
    }

    private fun createScreen(
        config: RootDestinationConfig,
        componentContext: ComponentContext,
    ): Child =
        when (config) {
            is RootDestinationConfig.Home ->
                Child.Home(
                    presenter = homePresenterFactory(
                        componentContext = componentContext,
                        onShowClicked = { id ->
                            navigator.pushNew(
                                RootDestinationConfig.ShowDetails(
                                    param = ShowDetailsParam(id = id),
                                ),
                            )
                        },
                        onMoreShowClicked = { id ->
                            navigator.pushNew(
                                RootDestinationConfig.MoreShows(
                                    id,
                                ),
                            )
                        },
                        onShowGenreClicked = { id ->
                            navigator.pushNew(
                                RootDestinationConfig.GenreShows(
                                    id,
                                ),
                            )
                        },
                        onNavigateToProfile = { navigator.pushNew(RootDestinationConfig.Profile) },
                        onSettingsClicked = { navigator.pushNew(RootDestinationConfig.Settings) },
                    ),
                )

            is RootDestinationConfig.Profile ->
                Child.Profile(
                    presenter = profilePresenterFactory(
                        componentContext = componentContext,
                        navigateBack = { navigator.pop() },
                        navigateToSettings = { navigator.pushNew(RootDestinationConfig.Settings) },
                    ),
                )

            is RootDestinationConfig.Settings ->
                Child.Settings(
                    presenter = settingsPresenterFactory(
                        componentContext = componentContext,
                        backClicked = navigator::pop,
                        onNavigateToDebugMenu = { navigator.pushNew(RootDestinationConfig.Debug) },
                    ),
                )

            is RootDestinationConfig.Debug ->
                Child.Debug(
                    presenter = debugPresenterFactory(
                        componentContext = componentContext,
                        backClicked = navigator::pop,
                    ),
                )

            is RootDestinationConfig.ShowDetails ->
                Child.ShowDetails(
                    presenter = showDetailsPresenterFactory(
                        componentContext = componentContext,
                        param = config.param,
                        onBack = navigator::pop,
                        onNavigateToShow = { id ->
                            navigator.pushToFront(
                                RootDestinationConfig.ShowDetails(
                                    param = ShowDetailsParam(id = id),
                                ),
                            )
                        },
                        onNavigateToSeason = { params ->
                            navigator.pushNew(
                                config = RootDestinationConfig.SeasonDetails(
                                    param = SeasonDetailsUiParam(
                                        showTraktId = params.showTraktId,
                                        seasonNumber = params.seasonNumber,
                                        seasonId = params.seasonId,
                                    ),
                                ),
                            )
                        },
                        onNavigateToTrailer = { id ->
                            navigator.pushNew(
                                RootDestinationConfig.Trailers(
                                    id,
                                ),
                            )
                        },
                        onShowFollowed = ::onShowFollowed,
                    ),
                )

            is RootDestinationConfig.SeasonDetails ->
                Child.SeasonDetails(
                    presenter = seasonDetailsPresenterFactory(
                        componentContext,
                        param = config.param,
                        onBack = navigator::pop,
                        onNavigateToEpisodeDetails = { _ ->
                            // TODO:: Navigate to episode details
                        },
                    ),
                )

            is RootDestinationConfig.Trailers ->
                Child.Trailers(
                    presenter = trailersPresenterFactory(
                        componentContext = componentContext,
                        traktShowId = config.id,
                    ),
                )

            is RootDestinationConfig.MoreShows ->
                Child.MoreShows(
                    presenter = moreShowsPresenterFactory(
                        componentContext = componentContext,
                        id = config.id,
                        onBack = navigator::pop,
                        onNavigateToShowDetails = { id ->
                            navigator.pushNew(
                                RootDestinationConfig.ShowDetails(
                                    param = ShowDetailsParam(id = id),
                                ),
                            )
                        },
                    ),
                )

            is RootDestinationConfig.GenreShows -> Child.GenreShows
        }

    @Inject
    @SingleIn(ActivityScope::class)
    @ContributesBinding(ActivityScope::class, RootPresenter.Factory::class)
    public class Factory(
        private val homePresenterFactory: HomePresenter.Factory,
        private val profilePresenterFactory: ProfilePresenter.Factory,
        private val settingsPresenterFactory: SettingsPresenter.Factory,
        private val debugPresenterFactory: DebugPresenter.Factory,
        private val moreShowsPresenterFactory: MoreShowsPresenter.Factory,
        private val showDetailsPresenterFactory: ShowDetailsPresenter.Factory,
        private val seasonDetailsPresenterFactory: SeasonDetailsPresenter.Factory,
        private val trailersPresenterFactory: TrailersPresenter.Factory,
        private val traktAuthRepository: TraktAuthRepository,
        private val updateUserProfileData: UpdateUserProfileData,
        private val logoutInteractor: LogoutInteractor,
        private val datastoreRepository: DatastoreRepository,
    ) : RootPresenter.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            navigator: RootNavigator,
        ): RootPresenter = DefaultRootPresenter(
            componentContext = componentContext,
            navigator = navigator,
            homePresenterFactory = homePresenterFactory,
            profilePresenterFactory = profilePresenterFactory,
            settingsPresenterFactory = settingsPresenterFactory,
            debugPresenterFactory = debugPresenterFactory,
            moreShowsPresenterFactory = moreShowsPresenterFactory,
            showDetailsPresenterFactory = showDetailsPresenterFactory,
            seasonDetailsPresenterFactory = seasonDetailsPresenterFactory,
            trailersPresenterFactory = trailersPresenterFactory,
            traktAuthRepository = traktAuthRepository,
            updateUserProfileData = updateUserProfileData,
            logoutInteractor = logoutInteractor,
            datastoreRepository = datastoreRepository,
        )
    }
}
