package com.thomaskioko.tvmaniac.debug.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.debug.nav.DebugRoute
import com.thomaskioko.tvmaniac.domain.continuewatching.SyncContinueWatchingInteractor
import com.thomaskioko.tvmaniac.domain.library.SyncLibraryInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleDebugEpisodeNotificationInteractor
import com.thomaskioko.tvmaniac.featureflags.nav.FeatureFlagsRoute
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.subscription.api.AccountType
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleDebugEpisodeNotificationInteractor.Params as DebugNotificationParams

@NavDestination(
    route = DebugRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@Inject
public class DebugPresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val datastoreRepository: DatastoreRepository,
    private val scheduleDebugEpisodeNotificationInteractor: ScheduleDebugEpisodeNotificationInteractor,
    private val syncLibraryInteractor: SyncLibraryInteractor,
    private val syncContinueWatchingInteractor: SyncContinueWatchingInteractor,
    private val dateTimeProvider: DateTimeProvider,
    private val localizer: Localizer,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    accountManager: AccountManager,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val debugNotificationState = ObservableLoadingCounter()
    private val delayedNotificationState = ObservableLoadingCounter()
    private val librarySyncState = ObservableLoadingCounter()
    private val upNextSyncState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    public val state: StateFlow<DebugState> = combine(
        debugNotificationState.observable,
        delayedNotificationState.observable,
        librarySyncState.observable,
        upNextSyncState.observable,
        datastoreRepository.observeLastSyncTimestamp(),
        datastoreRepository.observeLastUpNextSyncTimestamp(),
        datastoreRepository.observeLastTokenRefreshTimestamp(),
        datastoreRepository.observeAccountType(),
        uiMessageManager.message,
        accountManager.isConnected,
        accountManager.activeAuthState,
    ) {
            isSchedulingDebugNotification, isSchedulingDelayedNotification, isSyncingLibrary, isSyncingUpNext,
            lastLibrarySyncDate, lastUpNextSyncDate, lastTokenRefreshDate, accountTypeName, message, isLoggedIn, authState,
        ->
        val tokenSubtitle = formatTokenStatus(
            isLoggedIn = isLoggedIn,
            lastTokenRefreshTimestamp = lastTokenRefreshDate,
            authState = authState,
        )
        val accountType = AccountType.fromName(accountTypeName)

        DebugState(
            title = localizer.getString(StringResourceKey.LabelDebugMenuTitle),
            items = buildItems(
                isSchedulingDebugNotification = isSchedulingDebugNotification,
                isSchedulingDelayedNotification = isSchedulingDelayedNotification,
                isSyncingLibrary = isSyncingLibrary,
                isSyncingUpNext = isSyncingUpNext,
                lastLibrarySyncDate = lastLibrarySyncDate,
                lastUpNextSyncDate = lastUpNextSyncDate,
                tokenSubtitle = tokenSubtitle,
                accountType = accountType,
            ),
            isLoggedIn = isLoggedIn,
            accountType = accountType,
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DebugState.DEFAULT_STATE,
    )

    public val stateValue: Value<DebugState> = state.asValue(coroutineScope)

    public fun dispatch(action: DebugActions) {
        when (action) {
            BackClicked -> navigator.navigateBack()
            TriggerDebugNotification -> scheduleDebugNotification(loadingState = debugNotificationState)
            TriggerDelayedDebugNotification -> scheduleDebugNotification(
                duration = 5.minutes,
                loadingState = delayedNotificationState,
            )
            TriggerLibrarySync -> runIfLoggedIn { triggerLibrarySync() }
            TriggerUpNextSync -> runIfLoggedIn { triggerUpNextSync() }
            OpenFeatureFlags -> navigator.navigateTo(FeatureFlagsRoute)
            TriggerTestCrash -> throw RuntimeException("Test crash triggered from Debug Menu")
            is DismissSnackbar -> coroutineScope.launch { uiMessageManager.clearMessage(action.messageId) }
            is SetAccountType -> coroutineScope.launch {
                datastoreRepository.saveAccountType(
                    action.accountType.takeUnless { it == AccountType.None }?.name,
                )
            }
        }
    }

    private fun runIfLoggedIn(syncAction: () -> Unit) {
        if (state.value.isLoggedIn) {
            syncAction()
        } else {
            uiMessageManager.emitMessage(
                UiMessage(localizer.getString(StringResourceKey.LabelDebugSyncLoginRequired)),
            )
        }
    }

    private fun buildItems(
        isSchedulingDebugNotification: Boolean,
        isSchedulingDelayedNotification: Boolean,
        isSyncingLibrary: Boolean,
        isSyncingUpNext: Boolean,
        lastLibrarySyncDate: Long?,
        lastUpNextSyncDate: Long?,
        tokenSubtitle: String?,
        accountType: AccountType,
    ): ImmutableList<DebugItem> {
        val items = mutableListOf<DebugItem>()
        items += DebugItem(
            id = "account_type",
            icon = DebugItemIcon.Account,
            title = localizer.getString(StringResourceKey.LabelDebugAccountTypeTitle),
            subtitle = accountTypeLabel(accountType),
            action = null,
        )
        items += DebugItem(
            id = "notifications",
            icon = DebugItemIcon.Notifications,
            title = localizer.getString(StringResourceKey.LabelSettingsEpisodeNotifications),
            subtitle = localizer.getString(StringResourceKey.LabelSettingsDebugNotificationDescription),
            isLoading = isSchedulingDebugNotification,
            action = TriggerDebugNotification,
        )
        items += DebugItem(
            id = "delayed-notification",
            icon = DebugItemIcon.Schedule,
            title = localizer.getString(StringResourceKey.LabelSettingsDelayedDebugNotificationTitle),
            subtitle = localizer.getString(StringResourceKey.LabelSettingsDelayedDebugNotificationDescription),
            isLoading = isSchedulingDelayedNotification,
            action = TriggerDelayedDebugNotification,
        )
        items += DebugItem(
            id = "library-sync",
            icon = DebugItemIcon.LibrarySync,
            title = localizer.getString(StringResourceKey.LabelDebugLibrarySyncTitle),
            subtitle = syncSubtitle(lastLibrarySyncDate),
            isLoading = isSyncingLibrary,
            action = TriggerLibrarySync,
        )
        items += DebugItem(
            id = "upnext-sync",
            icon = DebugItemIcon.UpNextSync,
            title = localizer.getString(StringResourceKey.LabelDebugUpnextSyncTitle),
            subtitle = syncSubtitle(lastUpNextSyncDate),
            isLoading = isSyncingUpNext,
            action = TriggerUpNextSync,
        )
        items += DebugItem(
            id = "feature-flags",
            icon = DebugItemIcon.FeatureFlags,
            title = localizer.getString(StringResourceKey.LabelDebugFeatureFlagsTitle),
            subtitle = localizer.getString(StringResourceKey.LabelDebugFeatureFlagsDescription),
            action = OpenFeatureFlags,
        )
        if (tokenSubtitle != null) {
            items += DebugItem(
                id = "token-status",
                icon = DebugItemIcon.Key,
                title = localizer.getString(StringResourceKey.LabelDebugTokenStatusTitle),
                subtitle = tokenSubtitle,
                action = null,
            )
        }
        items += DebugItem(
            id = "test-crash",
            icon = DebugItemIcon.Warning,
            role = DebugItemRole.Destructive,
            title = localizer.getString(StringResourceKey.LabelDebugTriggerCrashTitle),
            subtitle = localizer.getString(StringResourceKey.LabelDebugTriggerCrashDescription),
            action = TriggerTestCrash,
        )
        return items.toImmutableList()
    }

    private fun accountTypeLabel(override: AccountType): String = when (override) {
        AccountType.Premium -> localizer.getString(StringResourceKey.LabelDebugAccountTypePremium)
        AccountType.Free -> localizer.getString(StringResourceKey.LabelDebugAccountTypeFree)
        AccountType.None -> localizer.getString(StringResourceKey.LabelDebugAccountTypeDescription)
    }

    private fun syncSubtitle(timestamp: Long?): String =
        if (timestamp != null) {
            localizer.getString(
                StringResourceKey.LabelSettingsLastSyncDate,
                dateTimeProvider.epochToDisplayDateTime(timestamp),
            )
        } else {
            localizer.getString(StringResourceKey.LabelDebugNeverSynced)
        }

    private fun scheduleDebugNotification(
        duration: Duration = Duration.ZERO,
        loadingState: ObservableLoadingCounter,
    ) {
        coroutineScope.launch {
            val notificationsEnabled = datastoreRepository.getEpisodeNotificationsEnabled()
            if (!notificationsEnabled) {
                datastoreRepository.setShowNotificationRationale(true)
                return@launch
            }
            scheduleDebugEpisodeNotificationInteractor(DebugNotificationParams(delay = duration))
                .collectStatus(loadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
    }

    private fun triggerLibrarySync() {
        coroutineScope.launch {
            syncLibraryInteractor(SyncLibraryInteractor.Param(forceRefresh = true, isUserInitiated = true))
                .collectStatus(librarySyncState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
    }

    private fun triggerUpNextSync() {
        coroutineScope.launch {
            syncContinueWatchingInteractor(SyncContinueWatchingInteractor.Param(forceRefresh = true))
                .collectStatus(upNextSyncState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
    }

    private fun formatTokenStatus(
        isLoggedIn: Boolean,
        lastTokenRefreshTimestamp: Long?,
        authState: AuthState?,
    ): String? {
        if (!isLoggedIn) return null

        val formattedDate = lastTokenRefreshTimestamp?.let(dateTimeProvider::epochToDisplayDateTime)
            ?: return localizer.getString(StringResourceKey.LabelDebugNeverRefreshed)

        val expiresAt = authState?.expiresAt
        val remaining = expiresAt?.minus(dateTimeProvider.now())
        return if (authState?.isAuthorized == true && remaining != null && remaining.isPositive()) {
            localizer.getString(
                StringResourceKey.LabelDebugTokenExpiresIn,
                formattedDate,
                formatRemaining(remaining),
            )
        } else {
            localizer.getString(StringResourceKey.LabelDebugTokenExpired, formattedDate)
        }
    }

    private fun formatRemaining(duration: Duration): String = when {
        duration.inWholeDays >= 1 -> "${duration.inWholeDays}d ${duration.inWholeHours % 24}h"
        duration.inWholeHours >= 1 -> "${duration.inWholeHours}h ${duration.inWholeMinutes % 60}m"
        duration.inWholeMinutes >= 1 -> "${duration.inWholeMinutes}m"
        else -> "${duration.inWholeSeconds.coerceAtLeast(0)}s"
    }
}
