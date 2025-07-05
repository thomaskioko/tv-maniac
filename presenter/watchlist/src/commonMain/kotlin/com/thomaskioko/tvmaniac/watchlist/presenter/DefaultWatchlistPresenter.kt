package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(ActivityScope::class, WatchlistPresenter::class)
class DefaultWatchlistPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigateToShowDetails: (id: Long) -> Unit,
    private val repository: WatchlistRepository,
    private val coroutineScope: CoroutineScope = componentContext.coroutineScope(),
) : WatchlistPresenter, ComponentContext by componentContext {

    private val queryFlow = MutableStateFlow("")
    private val _state = MutableStateFlow<WatchlistState>(LoadingShows)

    override val state: StateFlow<WatchlistState> = combine(
        queryFlow,
        repository.observeWatchlist(),
        repository.observeListStyle(),
    ) { query, result, isGridMode ->
        result.fold(
            onFailure = {
                EmptyWatchlist(
                    message = it.errorMessage ?: "Unknown error occurred",
                    query = query,
                    isSearchActive = query.isNotBlank(),
                    isGridMode = isGridMode,
                )
            },
            onSuccess = { shows ->
                WatchlistContent(
                    list = shows.entityToWatchlistShowList(),
                    query = query,
                    isSearchActive = query.isNotBlank(),
                    isGridMode = isGridMode,
                )
            },
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = _state.value,
    )

    override fun dispatch(action: WatchlistAction) {
        when (action) {
            is ReloadWatchlist -> refreshWatchlist()
            is WatchlistShowClicked -> navigateToShowDetails(action.id)
            is WatchlistQueryChanged -> updateQuery(action.query)
            is ClearWatchlistQuery -> clearQuery()
            ChangeListStyleClicked -> toggleListStyle()
        }
    }

    private fun refreshWatchlist() {
        coroutineScope.launch {
            observeWatchlist()
        }
    }

    private fun updateQuery(query: String) {
        coroutineScope.launch {
            queryFlow.emit(query)
            if (query.isNotBlank()) {
                searchWatchlist(query)
            } else {
                observeWatchlist()
            }
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

    private suspend fun searchWatchlist(query: String) {
        val currentIsGridMode = repository.observeListStyle().first()
        repository.searchWatchlistByQuery(query)
            .catch { handleError(it.message, query) }
            .collect { result ->
                handleSearchResult(query, result, currentIsGridMode)
            }
    }

    private suspend fun observeWatchlist() {
        val currentQuery = queryFlow.value
        val currentIsGridMode = repository.observeListStyle().first()
        repository.observeWatchlist()
            .onStart { _state.update { LoadingShows } }
            .catch { handleError(it.message, currentQuery) }
            .collect { result ->
                handleWatchlistResult(currentQuery, result, currentIsGridMode)
            }
    }

    private fun handleSearchResult(
        query: String,
        result: Either<Failure, List<SearchWatchlist>>,
        isGridMode: Boolean,
    ) {
        result.fold(
            onFailure = { handleError(it.errorMessage, query) },
            onSuccess = { shows ->
                val list = shows.entityToWatchlistShowList()
                _state.update {
                    WatchlistContent(
                        list = list,
                        query = query,
                        isSearchActive = true,
                        isGridMode = isGridMode,
                    )
                }
            },
        )
    }

    private fun handleWatchlistResult(
        query: String,
        result: Either<Failure, List<Watchlists>>,
        isGridMode: Boolean,
    ): WatchlistState {
        return result.fold(
            onFailure = {
                EmptyWatchlist(
                    message = it.errorMessage ?: "Unknown error occurred",
                    query = query,
                    isSearchActive = query.isNotBlank(),
                    isGridMode = isGridMode,
                )
            },
            onSuccess = { shows ->
                WatchlistContent(
                    list = shows.entityToWatchlistShowList(),
                    query = query,
                    isSearchActive = query.isNotBlank(),
                    isGridMode = isGridMode,
                )
            },
        )
    }

    private fun handleError(error: String?, query: String) {
        _state.update {
            EmptyWatchlist(
                message = error ?: "Unknown error occurred",
                query = query,
                isSearchActive = query.isNotBlank(),
            )
        }
    }
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, WatchlistPresenter.Factory::class)
class DefaultWatchlistPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
    ) -> WatchlistPresenter,
) : WatchlistPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
    ): WatchlistPresenter = presenter(componentContext, navigateToShowDetails)
}
