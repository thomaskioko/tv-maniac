package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.watchlist.WatchlistInteractor
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Inject
class WatchlistPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigateToShowDetails: (id: Long) -> Unit,
    private val repository: WatchlistRepository,
    private val refreshWatchlistInteractor: WatchlistInteractor,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val watchlistLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val coroutineScope = coroutineScope()
    private val queryFlow = MutableStateFlow("")
    private val watchListItemFlow: Flow<ImmutableList<WatchlistItem>> = queryFlow.flatMapLatest { query ->
        if (query.isNotBlank()) {
            repository.searchWatchlistByQuery(query).map { it.entityToWatchlistShowList() }
        } else {
            repository.observeWatchlist().map { it.entityToWatchlistShowList() }
        }
    }

    val state: StateFlow<WatchlistState> = combine(
        watchlistLoadingState.observable,
        watchListItemFlow,
        repository.observeListStyle(),
        uiMessageManager.message,
        queryFlow,
    ) { isLoading, watchlistItems, isGridMode, message, query ->
        WatchlistState(
            query = query,
            isSearchActive = query.isNotBlank(),
            isGridMode = isGridMode,
            isLoading = isLoading,
            items = watchlistItems,
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = WatchlistState(),
    )

    fun dispatch(action: WatchlistAction) {
        when (action) {
            is ReloadWatchlist -> refreshWatchlist()
            is WatchlistShowClicked -> navigateToShowDetails(action.id)
            is WatchlistQueryChanged -> updateQuery(action.query)
            is ClearWatchlistQuery -> clearQuery()
            ChangeListStyleClicked -> toggleListStyle()
            is MessageShown -> clearMessage(action.id)
        }
    }

    private fun refreshWatchlist() {
        coroutineScope.launch {
            refreshWatchlistInteractor(Unit)
                .collectStatus(watchlistLoadingState, logger, uiMessageManager)
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
        }
    }

    private fun clearQuery() {
        coroutineScope.launch {
            queryFlow.emit("")
        }
    }

    private fun toggleListStyle() {
        coroutineScope.launch {
            val currentIsGridMode = repository.observeListStyle().first()
            val newIsGridMode = !currentIsGridMode

            repository.saveListStyle(newIsGridMode)
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            @Assisted componentContext: ComponentContext,
            @Assisted navigateToShowDetails: (id: Long) -> Unit,
        ): WatchlistPresenter
    }
}
