package com.thomaskioko.tvmaniac.presentation.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.search.api.SearchRepository
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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

typealias SearchComponentFactory =
    (
    ComponentContext,
    onNavigateToShowDetails: (id: Long) -> Unit,
  ) -> SearchShowsComponent

@Inject
class SearchShowsComponent(
  @Assisted componentContext: ComponentContext,
  @Assisted private val onNavigateToShowDetails: (Long) -> Unit,
  private val mapper: ShowMapper,
  private val searchRepository: SearchRepository,
  private val featuredShowsRepository: FeaturedShowsRepository,
  private val trendingShowsRepository: TrendingShowsRepository,
  private val upcomingShowsRepository: UpcomingShowsRepository,
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
    private val _state = MutableStateFlow<SearchShowState>(ShowContentAvailable())
    val state: StateFlow<SearchShowState> = _state.asStateFlow()

    private val queryFlow = MutableSharedFlow<String>(
      replay = 1,
      onBufferOverflow = BufferOverflow.DROP_OLDEST,
      extraBufferCapacity = 1,
    )

    fun init() {
      coroutineScope.launch {
        launch { observeDiscoverShows() }
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
        ReloadShowContent -> coroutineScope.launch { observeDiscoverShows(refresh = true) }
        LoadDiscoverShows -> coroutineScope.launch { observeDiscoverShows() }
        ClearQuery -> coroutineScope.launch { observeDiscoverShows() }
        is QueryChanged -> handleQueryChange(action.query)
        is SearchShowClicked -> onNavigateToShowDetails(action.id)
      }
    }

    private suspend fun observeDiscoverShows(refresh: Boolean = false) {
      combine(
        featuredShowsRepository.observeFeaturedShows(forceRefresh = refresh),
        trendingShowsRepository.observeTrendingShows(forceRefresh = refresh),
        upcomingShowsRepository.observeUpcomingShows(forceRefresh = refresh),
      ) { featured, trending, upcoming ->
        Triple(featured, trending, upcoming)
      }
        .onStart { updateShowState() }
        .collect { (featured, trending, upcoming) ->
        val featuredShows = featured.getOrNull()
        val trendingShows = trending.getOrNull()
        val upcomingShows = upcoming.getOrNull()

        if (featuredShows.isNullOrEmpty() && trendingShows.isNullOrEmpty() && upcomingShows.isNullOrEmpty()) {
          _state.update { EmptySearchState(queryFlow.replayCache.lastOrNull()) }
        } else {
          _state.update {
            ShowContentAvailable(
              isUpdating = false,
              featuredShows = mapper.toShowList(featuredShows),
              trendingShows = mapper.toShowList(trendingShows),
              upcomingShows = mapper.toShowList(upcomingShows),
              errorMessage = mapper.getErrorMessage(featured, trending, upcoming),
            )
          }
        }
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
          _state.update { ErrorSearchState(errorMessage = error.message ?: "An unknown error occurred") }
        }
        .collect { result ->
          result.fold(
            onFailure = { handleErrorState(it) },
            onSuccess = { handleSearchResults(it) },
          )
        }
    }

    private fun updateSearchLoadingState(query: String) {
      _state.update {
        SearchResultAvailable(
          isUpdating = true,
          query = query,
          results = (it as? SearchResultAvailable)?.results,
        )
      }
    }

    private fun handleQueryChange(query: String) {
      coroutineScope.launch {
        if (query.isEmpty()) {
          observeDiscoverShows()
        } else {
          queryFlow.emit(query)
        }
      }
    }

    private fun handleErrorState(error: Failure) {
      _state.update {
        ErrorSearchState(errorMessage = error.errorMessage ?: "An unknown error occurred")
      }
    }

    private fun handleSearchResults(shows: List<ShowEntity>) {
      val state = when {
        shows.isEmpty() -> EmptySearchState(
          query = queryFlow.replayCache.lastOrNull(),
        )
        else -> SearchResultAvailable(
          isUpdating = false,
          results = mapper.toShowList(shows),
          query = queryFlow.replayCache.lastOrNull(),
        )
      }
      _state.update { state }
    }
  }
}
