package com.thomaskioko.tvmaniac.watchlist.presenter

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
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.ObserveUpNextSectionsInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.ObserveWatchlistSectionsInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.WatchlistSyncInteractor
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@AssistedInject
public class WatchlistPresenter(
    @Assisted componentContext: ComponentContext,
    private val navigator: WatchlistNavigator,
    private val repository: WatchlistRepository,
    private val unfollowShowInteractor: UnfollowShowInteractor,
    private val observeWatchlistSectionsInteractor: ObserveWatchlistSectionsInteractor,
    private val observeUpNextSectionsInteractor: ObserveUpNextSectionsInteractor,
    private val watchlistSyncInteractor: WatchlistSyncInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val watchlistLoadingState = ObservableLoadingCounter()
    private val upNextActionLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val coroutineScope = coroutineScope()
    private val queryFlow = MutableStateFlow("")
    private val _state = MutableStateFlow(WatchlistState())

    init {
        observeWatchlistSectionsInteractor(queryFlow.value)
        observeUpNextSectionsInteractor(queryFlow.value)
        syncWatchlist()
    }

    public val state: StateFlow<WatchlistState> = combine(
        _state,
        watchlistLoadingState.observable,
        upNextActionLoadingState.observable,
        observeWatchlistSectionsInteractor.flow,
        observeUpNextSectionsInteractor.flow,
        repository.observeListStyle(),
        uiMessageManager.message,
        queryFlow,
    ) { currentState, isLoading, upNextLoading, watchlistSections, upNextSections, isGridMode, message, query ->
        val sectionedItems = watchlistSections.toPresenter()
        val sectionedEpisodes = upNextSections.toPresenter()
        currentState.copy(
            query = query,
            isGridMode = isGridMode,
            isRefreshing = isLoading || upNextLoading,
            watchNextItems = sectionedItems.watchNext,
            staleItems = sectionedItems.stale,
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
            is WatchlistShowClicked -> navigator.showDetails(action.traktId)
            is WatchlistQueryChanged -> updateQuery(action.query)
            is ClearWatchlistQuery -> clearQuery()
            is ToggleSearchActive -> toggleSearchActive()
            is ChangeListStyleClicked -> toggleListStyle(action.isGridMode)
            is MessageShown -> clearMessage(action.id)
            is UpNextEpisodeClicked -> navigator.showDetails(action.showTraktId)
            is ShowTitleClicked -> navigator.showDetails(action.showTraktId)
            is MarkUpNextEpisodeWatched -> markEpisodeWatched(action)
            is UnfollowShowFromUpNext -> unfollowShow(action.showTraktId)
            is OpenSeasonFromUpNext -> navigator.showSeasonDetails(action.showTraktId, action.seasonId, action.seasonNumber)
            is RefreshWatchlist -> syncWatchlist(action.forceRefresh)
        }
    }

    private fun markEpisodeWatched(action: MarkUpNextEpisodeWatched) {
        coroutineScope.launch {
            markEpisodeWatchedInteractor(
                MarkEpisodeWatchedParams(
                    showTraktId = action.showTraktId,
                    episodeId = action.episodeId,
                    seasonNumber = action.seasonNumber,
                    episodeNumber = action.episodeNumber,
                ),
            ).collectStatus(upNextActionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
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

    private fun syncWatchlist(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            watchlistSyncInteractor(WatchlistSyncInteractor.Param(forceRefresh))
                .collectStatus(watchlistLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(
            componentContext: ComponentContext,
        ): WatchlistPresenter
    }
}
