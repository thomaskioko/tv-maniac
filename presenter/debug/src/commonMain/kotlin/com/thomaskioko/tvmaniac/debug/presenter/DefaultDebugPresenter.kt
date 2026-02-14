package com.thomaskioko.tvmaniac.debug.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.domain.library.SyncLibraryInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleDebugEpisodeNotificationInteractor
import com.thomaskioko.tvmaniac.domain.upnext.RefreshUpNextInteractor
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleDebugEpisodeNotificationInteractor.Params as DebugNotificationParams

@Inject
@ContributesBinding(ActivityScope::class, DebugPresenter::class)
public class DefaultDebugPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val backClicked: () -> Unit,
    private val datastoreRepository: DatastoreRepository,
    private val scheduleDebugEpisodeNotificationInteractor: ScheduleDebugEpisodeNotificationInteractor,
    private val syncLibraryInteractor: SyncLibraryInteractor,
    private val refreshUpNextInteractor: RefreshUpNextInteractor,
    private val dateTimeProvider: DateTimeProvider,
    private val logger: Logger,
) : DebugPresenter, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val debugNotificationState = ObservableLoadingCounter()
    private val librarySyncState = ObservableLoadingCounter()
    private val upNextSyncState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val snackbarMessage = MutableStateFlow<UiMessage?>(null)

    private val lastLibrarySyncDate = datastoreRepository.observeLastSyncTimestamp()
        .map { it?.let(dateTimeProvider::epochToDisplayDateTime) }

    private val lastUpNextSyncDate = datastoreRepository.observeLastUpNextSyncTimestamp()
        .map { it?.let(dateTimeProvider::epochToDisplayDateTime) }

    override val state: StateFlow<DebugState> = combine(
        debugNotificationState.observable,
        librarySyncState.observable,
        upNextSyncState.observable,
        lastLibrarySyncDate,
        lastUpNextSyncDate,
        uiMessageManager.message,
        snackbarMessage,
    ) { values ->
        DebugState(
            isSchedulingDebugNotification = values[0] as Boolean,
            isSyncingLibrary = values[1] as Boolean,
            isSyncingUpNext = values[2] as Boolean,
            lastLibrarySyncDate = values[3] as String?,
            lastUpNextSyncDate = values[4] as String?,
            message = values[6] as UiMessage?,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DebugState.DEFAULT_STATE,
    )

    override fun dispatch(action: DebugActions) {
        when (action) {
            BackClicked -> backClicked()
            TriggerDebugNotification -> scheduleDebugNotification()
            TriggerDelayedDebugNotification -> {
                snackbarMessage.value = UiMessage(message = NOTIFICATION_SCHEDULED_MESSAGE)
                scheduleDebugNotification(5.minutes)
            }
            TriggerLibrarySync -> triggerLibrarySync()
            TriggerUpNextSync -> triggerUpNextSync()
            is DismissSnackbar -> {
                snackbarMessage.value = null
            }
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
                .collectStatus(debugNotificationState, logger, uiMessageManager)
        }
    }

    private fun triggerLibrarySync() {
        coroutineScope.launch {
            syncLibraryInteractor(SyncLibraryInteractor.Param(forceRefresh = true))
                .collectStatus(librarySyncState, logger, uiMessageManager)
            datastoreRepository.setLastSyncTimestamp(dateTimeProvider.nowMillis())
        }
    }

    private fun triggerUpNextSync() {
        coroutineScope.launch {
            refreshUpNextInteractor(true)
                .collectStatus(upNextSyncState, logger, uiMessageManager)
        }
    }

    private companion object {
        const val NOTIFICATION_SCHEDULED_MESSAGE = "Notification scheduled in 5 minutes"
    }
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, DebugPresenter.Factory::class)
public class DefaultDebugPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        backClicked: () -> Unit,
    ) -> DebugPresenter,
) : DebugPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        backClicked: () -> Unit,
    ): DebugPresenter = presenter(componentContext, backClicked)
}
