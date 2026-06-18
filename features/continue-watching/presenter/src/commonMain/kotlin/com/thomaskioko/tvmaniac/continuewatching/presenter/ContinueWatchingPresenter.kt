package com.thomaskioko.tvmaniac.continuewatching.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveUpNextSectionsInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveWatchlistSectionsInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.SyncContinueWatchingInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.flags.ContinueWatchingNitroFlagQualifier
import com.thomaskioko.tvmaniac.myshows.nav.MyShowsRoot
import com.thomaskioko.tvmaniac.myshows.nav.scope.MyShowsChildScope
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.watchlistprefs.api.WatchlistPrefsRepository
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.ChildPresenter
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

@ChildPresenter(scope = MyShowsChildScope::class, parentScope = MyShowsRoot::class)
@Inject
public class ContinueWatchingPresenter(
    @ContinueWatchingNitroFlagQualifier
    nitroFlag: FeatureFlag<Boolean>,
    syncObserver: SyncObserver,
    repository: WatchlistPrefsRepository,
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val unfollowShowInteractor: UnfollowShowInteractor,
    private val observeWatchlistSectionsInteractor: ObserveWatchlistSectionsInteractor,
    private val observeUpNextSectionsInteractor: ObserveUpNextSectionsInteractor,
    private val syncContinueWatchingInteractor: SyncContinueWatchingInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val mapper: ContinueWatchingMapper,
    private val logger: Logger,
    private val accountManager: AccountManager,
) : ComponentContext by componentContext {

    private val watchlistLoadingState = ObservableLoadingCounter()
    private val userRefreshState = ObservableLoadingCounter()
    private val upNextActionLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val coroutineScope = coroutineScope()
    private val queryFlow = MutableStateFlow("")
    private val _state = MutableStateFlow(ContinueWatchingState())

    // TODO:: This is an experiment. Move to repository
    private val nitroEnabled: StateFlow<Boolean> = nitroFlag
        .observe()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = false,
        )

    init {
        observeWatchlistSectionsInteractor(queryFlow.value)
        observeUpNextSectionsInteractor(queryFlow.value)
        observeAuthState()
    }

    private fun observeAuthState() {
        coroutineScope.launch {
            accountManager.isConnected
                .distinctUntilChanged()
                .filter { it }
                .collect { syncWatchlist(forceRefresh = false) }
        }
    }

    public val state: StateFlow<ContinueWatchingState> = combine(
        _state,
        userRefreshState.observable,
        observeWatchlistSectionsInteractor.flow,
        observeUpNextSectionsInteractor.flow,
        repository.observeListStyle(),
        repository.observeSortOption(),
        uiMessageManager.message,
        queryFlow,
        syncObserver.isSyncing,
        watchlistLoadingState.observable,
    ) { currentState, isUserRefreshing, watchlistSections, upNextSections, isGridMode, sortOption, message, query, isSyncing, isLoading ->
        val sectionedItems = mapper.toSectionedItems(watchlistSections, sortOption)
        val sectionedEpisodes = mapper.toSectionedEpisodes(upNextSections)
        currentState.copy(
            query = query,
            isGridMode = isGridMode,
            isLoading = isLoading,
            isRefreshing = isUserRefreshing,
            isSyncing = isSyncing,
            labels = mapper.resolveLabels(query),
            watchNextItems = sectionedItems.watchNext,
            staleItems = sectionedItems.stale,
            watchNextEpisodes = sectionedEpisodes.watchNext,
            staleEpisodes = sectionedEpisodes.stale,
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ContinueWatchingState(),
    )

    public val stateValue: Value<ContinueWatchingState> = state.asValue(coroutineScope)

    public fun dispatch(action: ContinueWatchingAction) {
        when (action) {
            is ContinueWatchingShowClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
            is ContinueWatchingMessageShown -> clearMessage(action.id)
            is UpNextEpisodeClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
            is ShowTitleClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
            is MarkUpNextEpisodeWatched -> markEpisodeWatched(action)
            is UnfollowShowFromUpNext -> unfollowShow(action.showId)
            is OpenSeasonFromUpNext -> navigator.navigateTo(
                SeasonDetailsRoute(
                    SeasonDetailsUiParam(
                        showId = action.showId,
                        seasonId = action.seasonId,
                        seasonNumber = action.seasonNumber,
                    ),
                ),
            )

            is RefreshContinueWatching -> syncWatchlist(action.forceRefresh)
        }
    }

    private fun markEpisodeWatched(action: MarkUpNextEpisodeWatched) {
        if (action.episodeId in _state.value.updatingEpisodeIds) return
        _state.update { it.copy(updatingEpisodeIds = (it.updatingEpisodeIds + action.episodeId).toPersistentSet()) }
        coroutineScope.launch {
            val marker = TimeSource.Monotonic.markNow()
            try {
                markEpisodeWatchedInteractor(
                    MarkEpisodeWatchedParams(
                        showId = action.showId,
                        episodeId = action.episodeId,
                        seasonNumber = action.seasonNumber,
                        episodeNumber = action.episodeNumber,
                    ),
                ).collectStatus(
                    upNextActionLoadingState,
                    logger,
                    uiMessageManager,
                    errorToStringMapper = errorToStringMapper,
                )
            } finally {
                val elapsed = marker.elapsedNow()
                if (elapsed < INDICATOR_FLOOR) {
                    delay(INDICATOR_FLOOR - elapsed)
                }
                _state.update { it.copy(updatingEpisodeIds = (it.updatingEpisodeIds - action.episodeId).toPersistentSet()) }
            }
        }
    }

    private companion object {
        private val INDICATOR_FLOOR: Duration = 150.milliseconds
    }

    private fun unfollowShow(showId: Long) {
        coroutineScope.launch {
            unfollowShowInteractor.executeSync(showId)
        }
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    public fun onQueryChanged(query: String) {
        coroutineScope.launch {
            queryFlow.emit(query)
            observeWatchlistSectionsInteractor(query)
            observeUpNextSectionsInteractor(query)
        }
    }

    private fun syncWatchlist(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            val counter = if (forceRefresh) userRefreshState else watchlistLoadingState
            syncContinueWatchingInteractor(
                SyncContinueWatchingInteractor.Param(
                    forceRefresh = forceRefresh,
                    useNitro = nitroEnabled.value,
                ),
            )
                .collectStatus(
                    counter = counter,
                    logger = logger,
                    uiMessageManager = uiMessageManager,
                    errorToStringMapper = errorToStringMapper,
                )
        }
    }
}
