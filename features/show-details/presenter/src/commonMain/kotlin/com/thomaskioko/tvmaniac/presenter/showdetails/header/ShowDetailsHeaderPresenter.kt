package com.thomaskioko.tvmaniac.presenter.showdetails.header

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.root.nav.NotificationRationale
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.ProviderFeatures
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.SyncCalendarInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.FollowShowInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.presenter.showdetails.toHeaderState
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.scope.ShowDetailsChildScope
import com.thomaskioko.tvmaniac.showlist.nav.ShowListParam
import com.thomaskioko.tvmaniac.showlist.nav.ShowListRoute
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.ChildPresenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@ChildPresenter(scope = ShowDetailsChildScope::class, parentScope = ShowDetailsRoute::class)
@AssistedInject
public class ShowDetailsHeaderPresenter(
    componentContext: ComponentContext,
    @Assisted private val showId: Long,
    @Assisted private val forceRefresh: Boolean,
    private val navigator: Navigator,
    private val notificationRationale: NotificationRationale,
    private val followedShowsRepository: FollowedShowsRepository,
    private val followShowInteractor: FollowShowInteractor,
    private val showDetailsInteractor: ShowDetailsInteractor,
    observableShowDetailsInteractor: ObservableShowDetailsInteractor,
    private val syncCalendarInteractor: SyncCalendarInteractor,
    private val scheduleEpisodeNotificationsInteractor: ScheduleEpisodeNotificationsInteractor,
    private val notificationManager: NotificationManager,
    private val accountManager: AccountManager,
    private val activeProviderFeatures: () -> ProviderFeatures,
    private val localizer: Localizer,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val loadingState = ObservableLoadingCounter()
    private val followLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val _state = MutableStateFlow(ShowDetailsHeaderState())

    public val state: StateFlow<ShowDetailsHeaderState> = _state.asStateFlow()
    public val stateValue: Value<ShowDetailsHeaderState> = state.asValue(coroutineScope)

    init {
        observableShowDetailsInteractor(showId)

        observableShowDetailsInteractor.flow
            .onEach { details ->
                _state.update { current ->
                    details.toHeaderState(localizer).copy(
                        canAddToList = current.canAddToList,
                        isRefreshing = current.isRefreshing,
                        message = current.message,
                    )
                }
            }
            .launchIn(coroutineScope)

        loadingState.observable
            .onEach { refreshing -> _state.update { it.copy(isRefreshing = refreshing) } }
            .launchIn(coroutineScope)

        uiMessageManager.message
            .onEach { message -> _state.update { it.copy(message = message) } }
            .launchIn(coroutineScope)

        fetchShowDetails(forceRefresh = forceRefresh)
        observeAuthState()
        updateListAvailability()
    }

    public fun dispatch(action: ShowDetailsHeaderAction) {
        when (action) {
            is ShowDetailsFollowClicked -> onFollowClicked(action.isInLibrary)
            ShowDetailsOpenShowList -> if (_state.value.canAddToList) {
                navigator.navigateTo(ShowListRoute(ShowListParam(showId = showId)))
            }
        }
    }

    public fun refresh() {
        fetchShowDetails(forceRefresh = true)
    }

    public fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    private fun onFollowClicked(isInLibrary: Boolean) {
        coroutineScope.launch {
            if (isInLibrary) {
                followedShowsRepository.removeFollowedShow(showId)
                notificationManager.cancelNotificationsForShow(showId)
            } else {
                followShowInteractor(FollowShowInteractor.Param(showId = showId))
                    .collectStatus(followLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

                syncCalendarInteractor(SyncCalendarInteractor.Params(forceRefresh = true))
                    .collectStatus(followLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

                scheduleEpisodeNotificationsInteractor(ScheduleEpisodeNotificationsInteractor.Params())
                    .collectStatus(followLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

                notificationRationale.showIfNeeded()
            }
        }
    }

    private fun fetchShowDetails(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            showDetailsInteractor(ShowDetailsInteractor.Param(showId, forceRefresh))
                .collectStatus(loadingState, logger, uiMessageManager, "Show Details", errorToStringMapper)
        }
    }

    private fun observeAuthState() {
        coroutineScope.launch {
            accountManager.isConnected
                .drop(1)
                .distinctUntilChanged()
                .filter { it }
                .collect {
                    updateListAvailability()
                    fetchShowDetails(forceRefresh = true)
                }
        }
    }

    private fun updateListAvailability() {
        val canAddToList = accountManager.getActiveProvider() == null || activeProviderFeatures().supportsLists
        _state.update { it.copy(canAddToList = canAddToList) }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(showId: Long, forceRefresh: Boolean): ShowDetailsHeaderPresenter
    }
}
