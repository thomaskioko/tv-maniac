package com.thomaskioko.tvmaniac.presentation.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.genre.GenreRepository
import com.thomaskioko.tvmaniac.search.api.SearchRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SearchPresenterFactory(
  val create: (
    componentContext: ComponentContext,
    onNavigateToShowDetails: (id: Long) -> Unit,
    onNavigateToGenre: (id: Long) -> Unit,
  ) -> SearchShowsPresenter,
)

@Inject
class SearchShowsPresenter(
  @Assisted componentContext: ComponentContext,
  @Assisted private val onNavigateToShowDetails: (Long) -> Unit,
  @Assisted private val onNavigateToGenre: (Long) -> Unit,
  private val mapper: ShowMapper,
  private val searchRepository: SearchRepository,
  private val genreRepository: GenreRepository,
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
    private val _state = MutableStateFlow<SearchShowState>(InitialSearchState())
    val state: StateFlow<SearchShowState> = _state.asStateFlow()

    private val queryFlow = MutableSharedFlow<String>(
      replay = 1,
      onBufferOverflow = BufferOverflow.DROP_OLDEST,
      extraBufferCapacity = 1,
    )

    fun init() {
      coroutineScope.launch {
        launch { observeGenre() }
        launch { observeQueryFlow() }
      }
    }

    fun dispatch(action: SearchShowAction) {
      when (action) {
        DismissSnackBar -> {
          _state.update {
            (it as? ShowContentAvailable)?.copy(errorMessage = null) ?: it
          }
        }
        ReloadShowContent -> coroutineScope.launch { observeGenre(refresh = true) }
        LoadDiscoverShows, ClearQuery -> coroutineScope.launch { observeGenre() }
        is QueryChanged -> handleQueryChange(action.query)
        is SearchShowClicked -> onNavigateToShowDetails(action.id)
        is GenreCategoryClicked -> onNavigateToGenre(action.id)
      }
    }

    private suspend fun observeGenre(refresh: Boolean = false) {
      genreRepository.observeGenresWithShows(refresh)
        .onStart { updateShowState() }
        .collect { result ->
          result.fold(
            onFailure = { handleErrorState(it) },
            onSuccess = { list ->
              _state.update {
                ShowContentAvailable(
                  isUpdating = false,
                  genres = mapper.toGenreList(list),
                )
              }
            },
          )
        }
    }

    private fun updateShowState() {
      _state.update { currentState ->
        when (currentState) {
          is ShowContentAvailable -> currentState.copy(isUpdating = true)
          else -> ShowContentAvailable(isUpdating = true)
        }
      }
    }

    private suspend fun observeQueryFlow() {
      queryFlow
        .distinctUntilChanged()
        .debounce(300)
        .filter { it.trim().length >= 3 }
        .onEach { query -> updateSearchLoadingState(query) }
        .flatMapLatest { query -> searchRepository.search(query) }
        .catch { error ->
          _state.update {
            EmptySearchResult(
              query = queryFlow.replayCache.lastOrNull(),
              errorMessage = error.message ?: "An unknown error occurred",
            )
          }
        }
        .collect { result ->
          result.fold(
            onFailure = { handleErrorState(it) },
            onSuccess = { handleSearchResults(it) },
          )
        }
    }

    private fun updateSearchLoadingState(query: String) {
      _state.update { state ->
        when (state) {
          is SearchResultAvailable -> state.copy(isUpdating = true, query = query)
          else -> SearchResultAvailable(isUpdating = true, query = query)
        }
      }
    }

    private fun handleQueryChange(query: String) {
      coroutineScope.launch {
        if (query.isEmpty()) {
          observeGenre()
        } else {
          queryFlow.emit(query)
        }
      }
    }

    private fun handleErrorState(error: Failure) {
      _state.update { state ->
        EmptySearchResult(
          query = state.query,
          errorMessage = error.errorMessage ?: "An unknown error occurred"
        )
      }
    }

    private fun handleSearchResults(shows: List<ShowEntity>) {
      _state.update { state ->
        val currentQuery = queryFlow.replayCache.lastOrNull() ?: state.query
        when {
          !state.isUpdating && shows.isEmpty() -> EmptySearchResult(
            query = currentQuery
          )
          state is SearchResultAvailable -> state.copy(
            isUpdating = false,
            results = mapper.toShowList(shows),
            query = currentQuery
          )
          else -> SearchResultAvailable(
            isUpdating = false,
            results = mapper.toShowList(shows),
            query = currentQuery
          )
        }
      }
    }
  }
}
