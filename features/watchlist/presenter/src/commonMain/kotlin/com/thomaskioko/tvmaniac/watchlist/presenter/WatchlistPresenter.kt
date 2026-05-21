package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
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
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.watchlist.nav.WatchlistRoot
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import com.thomaskioko.tvmaniac.watchlistprefs.api.WatchlistPrefsRepository
import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
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

@NavDestination(
    route = WatchlistRoot::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.TAB_ROOT,
)
@Inject
public class WatchlistPresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val repository: WatchlistPrefsRepository,
    private val unfollowShowInteractor: UnfollowShowInteractor,
    private val observeWatchlistSectionsInteractor: ObserveWatchlistSectionsInteractor,
    private val observeUpNextSectionsInteractor: ObserveUpNextSectionsInteractor,
    private val syncContinueWatchingInteractor: SyncContinueWatchingInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val localizer: Localizer,
    private val logger: Logger,
    private val traktAuthRepository: TraktAuthRepository,
    @ContinueWatchingNitroFlagQualifier
    nitroFlag: FeatureFlag<Boolean>,
    syncObserver: SyncObserver,
) : ComponentContext by componentContext {

    private val watchlistLoadingState = ObservableLoadingCounter()
    private val userRefreshState = ObservableLoadingCounter()
    private val upNextActionLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val coroutineScope = coroutineScope()
    private val queryFlow = MutableStateFlow("")
    private val _state = MutableStateFlow(WatchlistState())

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
            traktAuthRepository.state
                .distinctUntilChanged()
                .filter { it == TraktAuthState.LOGGED_IN }
                .collect { syncWatchlist(forceRefresh = false) }
        }
    }

    public val state: StateFlow<WatchlistState> = combine(
        _state,
        userRefreshState.observable,
        observeWatchlistSectionsInteractor.flow,
        observeUpNextSectionsInteractor.flow,
        repository.observeListStyle(),
        repository.observeSortOption(),
        uiMessageManager.message,
        queryFlow,
        syncObserver.isSyncing,
    ) { currentState, isUserRefreshing, watchlistSections, upNextSections, isGridMode, sortOption, message, query, isSyncing ->

        // TODO:: Move to Mapper object and inject the mapper in the presenter
        val sectionedItems = watchlistSections.toPresenter()
        val sectionedEpisodes = upNextSections.toPresenter()
        val emptyStateKey = if (query.isBlank()) {
            StringResourceKey.LabelWatchlistEmptyInProgress
        } else {
            StringResourceKey.GenericEmptyContent
        }
        currentState.copy(
            query = query,
            isGridMode = isGridMode,
            isRefreshing = isUserRefreshing,
            isSyncing = isSyncing,
            sortOption = sortOption,
            emptyStateText = localizer.getString(emptyStateKey),
            watchNextItems = sectionedItems.watchNext.applySorting(sortOption),
            staleItems = sectionedItems.stale.applySorting(sortOption),
            watchNextEpisodes = sectionedEpisodes.watchNext,
            staleEpisodes = sectionedEpisodes.stale,
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = WatchlistState(),
    )

    public val stateValue: Value<WatchlistState> = state.asValue(coroutineScope)

    public fun dispatch(action: WatchlistAction) {
        when (action) {
            is WatchlistShowClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(id = action.traktId)))
            is WatchlistQueryChanged -> updateQuery(action.query)
            is ClearWatchlistQuery -> clearQuery()
            is ToggleSearchActive -> toggleSearchActive()
            is ChangeListStyleClicked -> toggleListStyle(action.isGridMode)
            is ChangeWatchlistSortOption -> changeSortOption(action.sortOption)
            is WatchlistMessageShown -> clearMessage(action.id)
            is UpNextEpisodeClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(id = action.showTraktId)))
            is ShowTitleClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(id = action.showTraktId)))
            is MarkUpNextEpisodeWatched -> markEpisodeWatched(action)
            is UnfollowShowFromUpNext -> unfollowShow(action.showTraktId)
            is OpenSeasonFromUpNext -> navigator.navigateTo(
                SeasonDetailsRoute(
                    SeasonDetailsUiParam(
                        showTraktId = action.showTraktId,
                        seasonId = action.seasonId,
                        seasonNumber = action.seasonNumber,
                    ),
                ),
            )

            is RefreshWatchlist -> syncWatchlist(action.forceRefresh)
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
                        showTraktId = action.showTraktId,
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

    private fun unfollowShow(showTraktId: Long) {
        coroutineScope.launch {
            unfollowShowInteractor.executeSync(showTraktId)
        }
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    private fun updateQuery(query: String) {
        coroutineScope.launch {
            queryFlow.emit(query)
            observeWatchlistSectionsInteractor(query)
            observeUpNextSectionsInteractor(query)
        }
    }

    private fun clearQuery() {
        coroutineScope.launch {
            queryFlow.emit("")
            observeWatchlistSectionsInteractor("")
            observeUpNextSectionsInteractor("")
        }
    }

    private fun toggleSearchActive() {
        _state.update { it.copy(isSearchActive = !it.isSearchActive) }
    }

    private fun toggleListStyle(currentIsGridMode: Boolean) {
        coroutineScope.launch {
            repository.saveListStyle(!currentIsGridMode)
        }
    }

    private fun changeSortOption(sortOption: WatchlistSortOption) {
        coroutineScope.launch {
            repository.saveSortOption(sortOption)
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

private fun ImmutableList<WatchlistItem>.applySorting(
    sortOption: WatchlistSortOption,
): ImmutableList<WatchlistItem> = when (sortOption) {
    WatchlistSortOption.ADDED_DESC -> sortedByDescending { it.lastWatchedAt ?: 0L }
    WatchlistSortOption.ADDED_ASC -> sortedBy { it.lastWatchedAt ?: Long.MAX_VALUE }
    WatchlistSortOption.RELEASED_DESC -> sortedByDescending { it.year.orEmpty() }
    WatchlistSortOption.RELEASED_ASC -> sortedBy { it.year.orEmpty() }
    WatchlistSortOption.TITLE_ASC -> sortedBy { it.title.lowercase() }
    WatchlistSortOption.TITLE_DESC -> sortedByDescending { it.title.lowercase() }
}.toImmutableList()
