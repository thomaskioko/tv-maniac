package com.thomaskioko.tvmaniac.presentation.watchlist

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class WatchlistPresenterFactory(
  val create: (
    componentContext: ComponentContext,
    navigateToShowDetails: (showDetails: Long) -> Unit,
  ) -> WatchlistPresenter,
)

@Inject
class WatchlistPresenter(
  @Assisted componentContext: ComponentContext,
  @Assisted private val navigateToShowDetails: (id: Long) -> Unit,
  private val repository: WatchlistRepository,
  private val coroutineScope: CoroutineScope = componentContext.coroutineScope(),
) : ComponentContext by componentContext {

  private val queryFlow = MutableStateFlow("")
  private val _state = MutableStateFlow<WatchlistState>(LoadingShows)
  val state: StateFlow<WatchlistState> = _state.asStateFlow()

  init {
    coroutineScope.launch {
      combine(
        queryFlow,
        repository.observeWatchlist(),
      ) { query, result ->
        query to result
      }.collect { (query, result) ->
        handleWatchlistResult(query, result)
      }
    }
  }

  fun dispatch(action: WatchlistAction) {
    when (action) {
      is ReloadWatchlist -> refreshWatchlist()
      is WatchlistShowClicked -> navigateToShowDetails(action.id)
      is WatchlistQueryChanged -> updateQuery(action.query)
      is ClearWatchlistQuery -> clearQuery()
      ChangeListStyleClicked -> {
        //TODO:: Update settings with change style
      }
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
      }
    }
  }

  private fun clearQuery() {
    coroutineScope.launch {
      queryFlow.emit("")
      observeWatchlist()
    }
  }

  private suspend fun searchWatchlist(query: String) {
    repository.searchWatchlistByQuery(query)
      .catch { handleError(it.message) }
      .collect { result ->
        handleSearchResult(query, result)
      }
  }

  private suspend fun observeWatchlist() {
    repository.observeWatchlist()
      .onStart { _state.update { LoadingShows } }
      .catch { handleError(it.message) }
      .collect { result ->
        handleWatchlistResult(queryFlow.replayCache.lastOrNull() ?: "", result)
      }
  }

  private fun handleSearchResult(query: String, result: Either<Failure, List<SearchWatchlist>>) {
    result.fold(
      onFailure = { handleError(it.errorMessage, query) },
      onSuccess = { shows ->
        val list = shows.entityToWatchlistShowList()

        _state.update { currentState ->
          (currentState as? WatchlistContent)?.copy(
            query = query,
            isSearchActive = true,
            list = list,
          ) ?: WatchlistContent(
            list = list,
            query = query,
            isSearchActive = true,
          )
        }
      },
    )
  }

  private fun handleWatchlistResult(query: String, result: Either<Failure, List<Watchlists>>) {
    result.fold(
      onFailure = { handleError(it.errorMessage, query) },
      onSuccess = { shows ->
        _state.update { currentState ->
          when (currentState) {
            is WatchlistContent -> currentState.copy(
              list = shows.entityToWatchlistShowList(),
              query = query,
              isSearchActive = query.isNotBlank(),
            )
            else -> WatchlistContent(
              list = shows.entityToWatchlistShowList(),
              query = query,
              isSearchActive = query.isNotBlank(),
            )
          }
        }
      },
    )
  }

  private fun handleError(error: String?, query: String = "") {
    _state.value = EmptyWatchlist(
      message = error ?: "Unknown error occurred",
      query = query,
      isSearchActive = query.isNotBlank(),
    )
  }
}
