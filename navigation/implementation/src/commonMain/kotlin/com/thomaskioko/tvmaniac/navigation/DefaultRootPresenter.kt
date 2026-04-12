package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.thomaskioko.nav.model.SeasonDetailsUiParam
import com.thomaskioko.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.core.base.extensions.asStateFlow
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.componentCoroutineScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.domain.logout.LogoutInteractor
import com.thomaskioko.tvmaniac.domain.user.UpdateUserProfileData
import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(kotlinx.coroutines.FlowPreview::class)
@AssistedInject
public class DefaultRootPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigator: RootNavigator,
    private val screenGraphFactory: ScreenGraph.Factory,
    private val episodeSheetController: EpisodeSheetController,
    private val traktAuthRepository: TraktAuthRepository,
    private val updateUserProfileData: UpdateUserProfileData,
    private val logoutInteractor: LogoutInteractor,
    private val logger: Logger,
    private val datastoreRepository: DatastoreRepository,
) : RootPresenter, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val profileLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

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
            traktAuthRepository.authError
                .filterIsInstance<AuthError.TokenExpired>()
                .collectLatest {
                    when (traktAuthRepository.refreshTokens()) {
                        is TokenRefreshResult.Success -> traktAuthRepository.setAuthError(null)
                        else -> logoutInteractor.executeSync(Unit)
                    }
                }
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
            .collectStatus(profileLoadingState, logger, uiMessageManager)
    }

    private val childStackRouter: Value<ChildStack<*, RootChild>> = childStack(
        source = navigator.getStackNavigation(),
        key = "RootChildStackKey",
        initialConfiguration = RootDestinationConfig.Home,
        serializer = RootDestinationConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createScreen,
    )

    override val childStack: StateFlow<ChildStack<*, RootChild>> =
        childStackRouter.asStateFlow(componentContext.componentCoroutineScope())

    override val childStackValue: Value<ChildStack<*, RootChild>> =
        childStack.asValue(coroutineScope)

    private val episodeSheetSlotRouter: Value<ChildSlot<*, SheetChild>> = childSlot(
        source = episodeSheetController.getSlotNavigation(),
        key = "EpisodeSheetSlotKey",
        serializer = EpisodeSheetConfig.serializer(),
        handleBackButton = true,
    ) { config, childComponentContext ->
        EpisodeSheetChild(
            presenter = screenGraphFactory.createGraph(childComponentContext)
                .episodeDetailFactory.create(config.episodeId, config.source),
        )
    }

    override val episodeSheetSlot: StateFlow<ChildSlot<*, SheetChild>> =
        episodeSheetSlotRouter.asStateFlow(componentContext.componentCoroutineScope())

    override val episodeSheetSlotValue: Value<ChildSlot<*, SheetChild>> =
        episodeSheetSlot.asValue(coroutineScope)

    override val themeState: StateFlow<ThemeState> =
        datastoreRepository
            .observeTheme()
            .map { theme -> ThemeState(isFetching = false, appTheme = theme) }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ThemeState(),
            )

    override val themeStateValue: Value<ThemeState> = themeState.asValue(coroutineScope)

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

    override val notificationPermissionStateValue: Value<NotificationPermissionState> =
        notificationPermissionState.asValue(coroutineScope)

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
            datastoreRepository.setNotificationPermissionAsked(true)
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
    ): RootChild {
        val screen = screenGraphFactory.createGraph(componentContext)
        return when (config) {
            is RootDestinationConfig.Home -> RootScreen.Home(screen.homePresenter)
            is RootDestinationConfig.Search -> RootScreen.Search(screen.searchPresenter)
            is RootDestinationConfig.Settings -> RootScreen.Settings(screen.settingsPresenter)
            is RootDestinationConfig.Debug -> RootScreen.Debug(screen.debugPresenter)
            is RootDestinationConfig.ShowDetails -> RootScreen.ShowDetails(screen.showDetailsFactory.create(config.param))
            is RootDestinationConfig.SeasonDetails -> RootScreen.SeasonDetails(screen.seasonDetailsFactory.create(config.param))
            is RootDestinationConfig.Trailers -> RootScreen.Trailers(screen.trailersFactory.create(config.id))
            is RootDestinationConfig.MoreShows -> RootScreen.MoreShows(screen.moreShowsFactory.create(config.id))
            is RootDestinationConfig.GenreShows -> RootScreen.GenreShows
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(componentContext: ComponentContext, navigator: RootNavigator): DefaultRootPresenter
    }
}
