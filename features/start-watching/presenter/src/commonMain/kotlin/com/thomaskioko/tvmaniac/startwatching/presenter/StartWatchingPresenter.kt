package com.thomaskioko.tvmaniac.startwatching.presenter

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
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.domain.startwatching.ObserveStartWatchingInteractor
import com.thomaskioko.tvmaniac.domain.startwatching.SyncStartWatchingFirstSeasonInteractor
import com.thomaskioko.tvmaniac.myshows.nav.MyShowsRoot
import com.thomaskioko.tvmaniac.myshows.nav.scope.MyShowsChildScope
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.watchlistprefs.api.WatchlistPrefsRepository
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.ChildPresenter
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@ChildPresenter(scope = MyShowsChildScope::class, parentScope = MyShowsRoot::class)
@Inject
public class StartWatchingPresenter(
    componentContext: ComponentContext,
    repository: WatchlistPrefsRepository,
    observeStartWatchingInteractor: ObserveStartWatchingInteractor,
    private val syncObserver: SyncObserver,
    private val navigator: Navigator,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val syncStartWatchingFirstSeasonInteractor: SyncStartWatchingFirstSeasonInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val queryFlow = MutableStateFlow("")
    private val markWatchedLoadingState = ObservableLoadingCounter()
    private val userRefreshState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val _state = MutableStateFlow(StartWatchingState())

    init {
        observeStartWatchingInteractor(Unit)
        observeShowsForFirstSeasonSync(observeStartWatchingInteractor)
    }

    public val state: StateFlow<StartWatchingState> = combine(
        _state,
        observeStartWatchingInteractor.flow,
        queryFlow,
        repository.observeSortOption(),
        repository.observeListStyle(),
        uiMessageManager.message,
        syncObserver.isSyncing,
        userRefreshState.observable,
    ) { currentState, shows, query, sortOption, isGridMode, message, isSyncing, isRefreshing ->
        currentState.copy(
            isSyncing = isSyncing,
            isRefreshing = isRefreshing,
            isGridMode = isGridMode,
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
            is StartWatchingShowClicked -> navigateToShowDetails(action.traktId)
            is StartWatchingEpisodeClicked -> navigateToShowDetails(action.showTraktId)
            is StartWatchingShowTitleClicked -> navigateToShowDetails(action.showTraktId)
            is MarkStartWatchingEpisodeWatched -> markEpisodeWatched(action)
            is StartWatchingMessageShown -> clearMessage(action.id)
            is RefreshStartWatching -> refresh()
        }
    }

    private fun navigateToShowDetails(traktId: Long) {
        navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(id = traktId)))
    }

    private fun observeShowsForFirstSeasonSync(interactor: ObserveStartWatchingInteractor) {
        coroutineScope.launch {
            interactor.flow
                .map { shows -> shows.map { it.traktId }.toSet() }
                .distinctUntilChanged()
                .collect { ids -> if (ids.isNotEmpty()) syncFirstSeason() }
        }
    }

    private fun markEpisodeWatched(action: MarkStartWatchingEpisodeWatched) {
        if (action.episodeId in _state.value.updatingEpisodeIds) return
        _state.update { it.copy(updatingEpisodeIds = (it.updatingEpisodeIds + action.episodeId).toPersistentSet()) }
        coroutineScope.launch {
            try {
                markEpisodeWatchedInteractor(
                    MarkEpisodeWatchedParams(
                        showTraktId = action.showTraktId,
                        episodeId = action.episodeId,
                        seasonNumber = action.seasonNumber,
                        episodeNumber = action.episodeNumber,
                    ),
                ).collectStatus(
                    markWatchedLoadingState,
                    logger,
                    uiMessageManager,
                    errorToStringMapper = errorToStringMapper,
                )
            } finally {
                _state.update { it.copy(updatingEpisodeIds = (it.updatingEpisodeIds - action.episodeId).toPersistentSet()) }
            }
        }
    }

    private fun syncFirstSeason(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            syncStartWatchingFirstSeasonInteractor.executeSync(
                SyncStartWatchingFirstSeasonInteractor.Param(forceRefresh = forceRefresh),
            )
        }
    }

    private fun refresh() {
        coroutineScope.launch {
            syncStartWatchingFirstSeasonInteractor(
                SyncStartWatchingFirstSeasonInteractor.Param(forceRefresh = true),
            ).collectStatus(
                userRefreshState,
                logger,
                uiMessageManager,
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
