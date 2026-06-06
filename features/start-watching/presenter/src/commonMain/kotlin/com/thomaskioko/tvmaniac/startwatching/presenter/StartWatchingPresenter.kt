package com.thomaskioko.tvmaniac.startwatching.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedAccountRepository
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.startwatching.ObserveStartWatchingInteractor
import com.thomaskioko.tvmaniac.domain.startwatching.SyncStartWatchingInteractor
import com.thomaskioko.tvmaniac.myshows.nav.MyShowsRoot
import com.thomaskioko.tvmaniac.myshows.nav.scope.MyShowsChildScope
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.watchlistprefs.api.WatchlistPrefsRepository
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.ChildPresenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ChildPresenter(scope = MyShowsChildScope::class, parentScope = MyShowsRoot::class)
@Inject
public class StartWatchingPresenter(
    componentContext: ComponentContext,
    repository: WatchlistPrefsRepository,
    observeStartWatchingInteractor: ObserveStartWatchingInteractor,
    private val syncObserver: SyncObserver,
    private val navigator: Navigator,
    private val syncStartWatchingInteractor: SyncStartWatchingInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    private val connectedAccountRepository: ConnectedAccountRepository,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val queryFlow = MutableStateFlow("")
    private val userRefreshState = ObservableLoadingCounter()
    private val watchlistLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    init {
        observeStartWatchingInteractor(Unit)
        observeAuthState()
    }

    private fun observeAuthState() {
        coroutineScope.launch {
            connectedAccountRepository.isConnected
                .distinctUntilChanged()
                .filter { it }
                .collect { syncStartWatching(forceRefresh = false) }
        }
    }

    public val state: StateFlow<StartWatchingState> = combine(
        observeStartWatchingInteractor.flow,
        queryFlow,
        repository.observeSortOption(),
        uiMessageManager.message,
        syncObserver.isSyncing,
        userRefreshState.observable,
    ) { shows, query, sortOption, message, isSyncing, isRefreshing ->
        StartWatchingState(
            isSyncing = isSyncing,
            isRefreshing = isRefreshing,
            items = shows.toStartWatchingItems(query, sortOption),
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = StartWatchingState(),
    )

    public val stateValue: Value<StartWatchingState> = state.asValue(coroutineScope)

    public fun onQueryChanged(query: String) {
        queryFlow.value = query
    }

    public fun dispatch(action: StartWatchingAction) {
        when (action) {
            is StartWatchingShowClicked -> navigateToShowDetails(action.showId)
            is StartWatchingMessageShown -> clearMessage(action.id)
            is RefreshStartWatching -> syncStartWatching(action.forceRefresh)
        }
    }

    private fun navigateToShowDetails(showId: Long) {
        navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = showId)))
    }

    private fun syncStartWatching(forceRefresh: Boolean) {
        coroutineScope.launch {
            val counter = if (forceRefresh) userRefreshState else watchlistLoadingState
            syncStartWatchingInteractor(
                SyncStartWatchingInteractor.Param(forceRefresh = forceRefresh),
            ).collectStatus(
                counter = counter,
                logger = logger,
                uiMessageManager = uiMessageManager,
                errorToStringMapper = errorToStringMapper,
            )
        }
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}
