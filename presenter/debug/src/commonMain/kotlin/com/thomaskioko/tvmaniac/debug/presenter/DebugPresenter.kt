package com.thomaskioko.tvmaniac.debug.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.domain.library.SyncLibraryInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleDebugEpisodeNotificationInteractor
import com.thomaskioko.tvmaniac.domain.upnext.RefreshUpNextInteractor
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleDebugEpisodeNotificationInteractor.Params as DebugNotificationParams

@Inject
public class DebugPresenter(
    componentContext: ComponentContext,
    private val navigator: DebugNavigator,
    private val datastoreRepository: DatastoreRepository,
    private val scheduleDebugEpisodeNotificationInteractor: ScheduleDebugEpisodeNotificationInteractor,
    private val syncLibraryInteractor: SyncLibraryInteractor,
    private val refreshUpNextInteractor: RefreshUpNextInteractor,
    private val dateTimeProvider: DateTimeProvider,
    private val localizer: Localizer,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    traktAuthRepository: TraktAuthRepository,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val debugNotificationState = ObservableLoadingCounter()
    private val librarySyncState = ObservableLoadingCounter()
    private val upNextSyncState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    public val state: StateFlow<DebugState> = combine(
        debugNotificationState.observable,
        librarySyncState.observable,
        upNextSyncState.observable,
        datastoreRepository.observeLastSyncTimestamp(),
        datastoreRepository.observeLastUpNextSyncTimestamp(),
        datastoreRepository.observeLastTokenRefreshTimestamp(),
        uiMessageManager.message,
        traktAuthRepository.state,
        traktAuthRepository.authState,
    ) {
            isSchedulingDebugNotification, isSyncingLibrary, isSyncingUpNext, lastLibrarySyncDate, lastUpNextSyncDate,
            lastTokenRefreshDate, message, isLoggedIn, authState,
        ->
        val isUserLoggedIn = isLoggedIn == TraktAuthState.LOGGED_IN
        DebugState(
            isSchedulingDebugNotification = isSchedulingDebugNotification,
            isSyncingLibrary = isSyncingLibrary,
            isSyncingUpNext = isSyncingUpNext,
            lastLibrarySyncDate = lastLibrarySyncDate?.let(dateTimeProvider::epochToDisplayDateTime),
            lastUpNextSyncDate = lastUpNextSyncDate?.let(dateTimeProvider::epochToDisplayDateTime),
            tokenStatusSubtitle = formatTokenStatus(
                isLoggedIn = isUserLoggedIn,
                lastTokenRefreshTimestamp = lastTokenRefreshDate,
                authState = authState,
            ),
            message = message,
            isLoggedIn = isUserLoggedIn,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DebugState.DEFAULT_STATE,
    )

    public val stateValue: Value<DebugState> = state.asValue(coroutineScope)

    public fun dispatch(action: DebugActions) {
        when (action) {
            BackClicked -> navigator.goBack()
            TriggerDebugNotification -> scheduleDebugNotification()
            TriggerDelayedDebugNotification -> scheduleDebugNotification(5.minutes)
            TriggerLibrarySync -> triggerLibrarySync()
            TriggerUpNextSync -> triggerUpNextSync()
            is DismissSnackbar -> coroutineScope.launch { uiMessageManager.clearMessage(action.messageId) }
        }
    }

    private fun scheduleDebugNotification(duration: Duration = Duration.ZERO) {
        coroutineScope.launch {
            val notificationsEnabled = datastoreRepository.getEpisodeNotificationsEnabled()
            if (!notificationsEnabled) {
                datastoreRepository.setShowNotificationRationale(true)
                return@launch
            }
            scheduleDebugEpisodeNotificationInteractor(DebugNotificationParams(delay = duration))
                .collectStatus(debugNotificationState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
    }

    private fun triggerLibrarySync() {
        coroutineScope.launch {
            syncLibraryInteractor(SyncLibraryInteractor.Param(forceRefresh = true))
                .collectStatus(librarySyncState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
    }

    private fun triggerUpNextSync() {
        coroutineScope.launch {
            refreshUpNextInteractor(true)
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

        val key = if (authState?.isAuthorized == true) {
            StringResourceKey.LabelDebugTokenRefreshValid
        } else {
            StringResourceKey.LabelDebugTokenRefreshExpired
        }
        return localizer.getString(key, formattedDate)
    }
}
