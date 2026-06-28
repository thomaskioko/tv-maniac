package com.thomaskioko.tvmaniac.presenter.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.thomaskioko.root.model.DeepLinkDestination
import com.thomaskioko.root.model.NotificationPermissionState
import com.thomaskioko.root.model.ThemeState
import com.thomaskioko.root.nav.NotificationRationale
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asStateFlow
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.componentCoroutineScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.base.extensions.minTrueDuration
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.debug.nav.DebugRoute
import com.thomaskioko.tvmaniac.domain.logout.LogoutInteractor
import com.thomaskioko.tvmaniac.domain.user.UpdateUserProfileData
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.ScreenDestination
import com.thomaskioko.tvmaniac.navigation.SheetChild
import com.thomaskioko.tvmaniac.navigation.SheetDestination
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.presenter.home.di.HomeScreenGraph
import com.thomaskioko.tvmaniac.presenter.root.model.ToastState
import com.thomaskioko.tvmaniac.presenter.root.model.ToastType
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.settings.presenter.toTheme
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.AppRoot
import kotlinx.coroutines.flow.MutableStateFlow
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import com.thomaskioko.tvmaniac.syncstate.api.SyncError as SyncStateError

@OptIn(kotlinx.coroutines.FlowPreview::class)
@AppRoot(parentScope = ActivityScope::class)
@AssistedInject
public class DefaultRootPresenter(
    @Assisted componentContext: ComponentContext,
    private val navigator: Navigator,
    private val navDestinations: Set<NavDestination<*>>,
    homeGraphFactory: HomeScreenGraph.Factory,
    private val notificationRationale: NotificationRationale,
    private val accountManager: AccountManager,
    private val updateUserProfileData: UpdateUserProfileData,
    private val logoutInteractor: LogoutInteractor,
    private val logger: Logger,
    private val datastoreRepository: DatastoreRepository,
    private val syncObserver: SyncObserver,
    private val localizer: Localizer,
) : RootPresenter, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val profileLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val syncErrorMessages = UiMessageManager()
    private val syncStatusDismissed = MutableStateFlow(false)
    private val accountLimitBannerState = MutableStateFlow(AccountLimitBannerState())

    init {
        coroutineScope.launch {
            accountManager.isConnected
                .debounce(200.milliseconds)
                .distinctUntilChanged()
                .filter { it }
                .collect { refreshUserProfile() }
        }

        coroutineScope.launch {
            accountManager.authError
                .filterIsInstance<AuthError.TokenExpired>()
                .collectLatest {
                    when (accountManager.refreshActiveTokens()) {
                        is TokenRefreshResult.Success -> accountManager.setAuthError(null)
                        else -> logoutInteractor.executeSync(Unit)
                    }
                }
        }

        coroutineScope.launch {
            accountManager.isConnected
                .debounce(500.milliseconds)
                .distinctUntilChanged()
                .filter { it }
                .take(1)
                .collect { notificationRationale.showIfNeeded() }
        }

        coroutineScope.launch {
            syncObserver.syncStarted.collect { syncStatusDismissed.update { false } }
        }

        coroutineScope.launch {
            syncObserver.errors.collect { error ->
                if (error is SyncStateError.AccountLimitExceeded) {
                    accountLimitBannerState.update { it.copy(errorOccurred = true) }
                } else {
                    syncErrorMessages.emitMessage(
                        UiMessage(message = localizer.getString(StringResourceKey.SyncFailedWillRetry)),
                    )
                    syncStatusDismissed.update { true }
                }
            }
        }
    }

    private suspend fun refreshUserProfile() {
        updateUserProfileData(UpdateUserProfileData.Params(forceRefresh = false))
            .collectStatus(profileLoadingState, logger, uiMessageManager)
    }

    override val homePresenter: HomePresenter = homeGraphFactory.createHomeGraph(this).homePresenter

    private val sheetSlotRouter: Value<ChildSlot<*, SheetChild>> = navigator.buildOverlaySlot(
        componentContext = this,
        childFactory = ::createOverlay,
    )

    override val episodeSheetSlot: StateFlow<ChildSlot<*, SheetChild>> =
        sheetSlotRouter.asStateFlow(componentContext.componentCoroutineScope())

    override val episodeSheetSlotValue: Value<ChildSlot<*, SheetChild>> =
        episodeSheetSlot.asValue(coroutineScope)

    override val themeState: StateFlow<ThemeState> =
        datastoreRepository
            .observeTheme()
            .map { theme -> ThemeState(isFetching = false, appTheme = theme.toTheme()) }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ThemeState(),
            )

    override val themeStateValue: Value<ThemeState> = themeState.asValue(coroutineScope)

    // TODO:: Move notification rationale to it's own feature.
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

    override val toastState: StateFlow<ToastState> = combine(
        syncObserver.isSyncing.minTrueDuration(MIN_STATUS_DISPLAY),
        syncStatusDismissed,
        syncErrorMessages.message,
    ) { syncing, dismissed, errorMessage ->
        when {
            errorMessage != null -> ToastState(
                message = errorMessage.message,
                type = ToastType.Error,
                persistent = false,
                id = errorMessage.id,
            )
            syncing && !dismissed -> ToastState(
                message = localizer.getString(StringResourceKey.SyncingLibrary),
                type = ToastType.Status,
                persistent = true,
            )
            else -> ToastState()
        }
    }
        .distinctUntilChanged()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ToastState(),
        )

    override val toastStateValue: Value<ToastState> = toastState.asValue(coroutineScope)

    override val accountLimitBannerVisible: StateFlow<Boolean> = accountLimitBannerState
        .map { state -> state.errorOccurred && !state.dismissed }
        .distinctUntilChanged()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    override val accountLimitBannerVisibleValue: Value<Boolean> =
        accountLimitBannerVisible.asValue(coroutineScope)

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
                navigator.navigateTo(
                    ShowDetailsRoute(
                        param = ShowDetailsParam(
                            showId = destination.showId,
                            forceRefresh = destination.forceRefresh,
                        ),
                    ),
                )
            }
            is DeepLinkDestination.SeasonDetails -> {
                navigator.navigateTo(
                    SeasonDetailsRoute(
                        param = SeasonDetailsUiParam(
                            showId = destination.showId,
                            seasonNumber = destination.seasonNumber,
                            seasonId = destination.seasonId,
                            forceRefresh = destination.forceRefresh,
                        ),
                    ),
                )
            }
            is DeepLinkDestination.DebugMenu -> {
                navigator.navigateTo(DebugRoute)
            }
        }
    }

    private fun createOverlay(
        route: NavRoute,
        componentContext: ComponentContext,
    ): SheetChild {
        val destination = navDestinations
            .filterIsInstance<NavDestination.Overlay<*>>()
            .firstOrNull { it.matches(route) }
            ?: error("No NavDestination.Overlay found for route: $route")
        return when (val rootChild = destination.createChild(route, componentContext)) {
            is ScreenDestination<*> -> SheetDestination(rootChild.presenter)
            else -> error(
                "NavDestination.Overlay produced unsupported child for route $route: ${rootChild::class.simpleName}",
            )
        }
    }

    override fun onToastShown(id: Long) {
        coroutineScope.launch {
            syncErrorMessages.clearMessage(id)
        }
    }

    override fun dismissSyncStatus() {
        syncStatusDismissed.update { true }
    }

    override fun onDismissAccountLimitBanner() {
        accountLimitBannerState.update { it.copy(dismissed = true) }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(componentContext: ComponentContext): DefaultRootPresenter
    }

    private companion object {
        private val MIN_STATUS_DISPLAY = 2_500.milliseconds
    }
}

private data class AccountLimitBannerState(
    val errorOccurred: Boolean = false,
    val dismissed: Boolean = false,
)
