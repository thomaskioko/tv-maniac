package com.thomaskioko.tvmaniac.presentation.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.search.api.SearchRepository
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias SearchComponentFactory =
    (
    ComponentContext,
    onNavigateToShowDetails: (id: Long) -> Unit,
    onNavigateToMore: (categoryId: Long) -> Unit,
  ) -> SearchShowsComponent

@Inject
class SearchShowsComponent(
  @Assisted componentContext: ComponentContext,
  @Assisted private val onNavigateToShowDetails: (Long) -> Unit,
  @Assisted private val onNavigateToMore: (Long) -> Unit,
  private val searchRepository: SearchRepository,
  private val coroutineScope: CoroutineScope = componentContext.coroutineScope(),
) : ComponentContext by componentContext {

  private val presenterInstance = instanceKeeper.getOrCreate { PresenterInstance() }
  val state: StateFlow<SearchShowState> = presenterInstance.state

  init {
    presenterInstance.init()
  }

  fun dispatch(action: SearchShowAction) {
    presenterInstance.dispatch(action)
  }

  internal inner class PresenterInstance : InstanceKeeper.Instance {
    private val _state = MutableStateFlow<SearchShowState>(InitialState())
    val state: StateFlow<SearchShowState> = _state.asStateFlow()

    private val queryFlow = MutableSharedFlow<String>(
      replay = 1,
      onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    fun init() {
      coroutineScope.launch {
        observeQueryFlow()
      }
    }

    fun dispatch(action: SearchShowAction) {
      coroutineScope.launch {
        when (action) {
          LoadDiscoverShows -> _state.update { InitialState() }
          ClearQuery -> queryFlow.emit("")
          is QueryChanged -> queryFlow.emit(action.query)
          is SearchShowClicked -> onNavigateToShowDetails(action.id)
        }
      }
    }



    private fun isEmpty(vararg responses: List<ShowEntity>?): Boolean {
      return responses.all { it.isNullOrEmpty() }
    }

    private suspend fun observeQueryFlow() {
      queryFlow
        .onEach { updateQuerySearchState(it) }
        .debounce(300)
        .distinctUntilChanged()
        .transformLatest { query ->
          if (query.trim().length >= 3) {
            emitAll(searchRepository.search(query))
          }
        }
        .catch { error ->
          _state.update { ErrorState(error.message ?: "An unknown error occurred") }
        }
        .collect { result ->
          result.fold(
            onFailure = { handleErrorState(it) },
            onSuccess = { handleSearchResults(it) },
          )
        }
    }

    private fun updateQuerySearchState(query: String) {
      when {
        query.isBlank() -> dispatch(LoadDiscoverShows)
        query.trim().length < 3 -> Unit
        else -> _state.update {
          SearchResultAvailable(
            isUpdating = true,
            result = (it as? SearchResultAvailable)?.result ?: persistentListOf(),
          )
        }
      }
    }

    private fun handleErrorState(error: Failure) {
      _state.update {
        ErrorState(errorMessage = error.errorMessage ?: "An unknown error occurred")
      }
    }

    private fun handleSearchResults(shows: List<ShowEntity>?) {
      _state.update {
        when {
          shows.isNullOrEmpty() -> EmptyState
          else -> SearchResultAvailable(
            isUpdating = false,
            result = shows.map {
              it.toSearchResult()
            }.toImmutableList(),
          )
        }
      }
    }

  }
}
