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
import kotlinx.collections.immutable.persistentListOf
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
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest
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
      coroutineScope.launch {
        when (action) {
          LoadDiscoverShows -> observeDiscoverShows()
          ClearQuery -> queryFlow.emit("")
          is QueryChanged -> queryFlow.emit(action.query)
          is SearchShowClicked -> onNavigateToShowDetails(action.id)
        }
      }
    }

    private suspend fun observeDiscoverShows() {
      combine(
        featuredShowsRepository.observeFeaturedShows(),
        trendingShowsRepository.observeTrendingShows(),
        upcomingShowsRepository.observeUpcomingShows(),
      ) { featured, trending, upcoming ->
        Triple(featured, trending, upcoming)
      }.onStart {
        _state.update { currentState ->
          when (currentState) {
            is ShowContentAvailable -> currentState.copy(isUpdating = true)
            else -> ShowContentAvailable(isUpdating = true)
          }
        }
      }.collect { (featured, trending, upcoming) ->
        val featuredShows = featured.getOrNull()
        val trendingShows = trending.getOrNull()
        val upcomingShows = upcoming.getOrNull()

        if (featuredShows.isNullOrEmpty() && trendingShows.isNullOrEmpty() && upcomingShows.isNullOrEmpty()) {
          _state.update { EmptyState }
        } else {
          _state.update {
            ShowContentAvailable(
              isUpdating = false,
              featuredShows = featuredShows?.toShowList() ?: persistentListOf(),
              trendingShows = trendingShows?.toShowList() ?: persistentListOf(),
              upcomingShows = upcomingShows?.toShowList() ?: persistentListOf(),
              errorMessage = getErrorMessage(featured, trending, upcoming),
            )
          }
        }
      }
    }

    private suspend fun observeQueryFlow() {
      queryFlow
        .distinctUntilChanged()
        .onEach { updateQuerySearchState(it) }
        .debounce(300)
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

    private suspend fun updateQuerySearchState(query: String) {
      when {
        query.isBlank() -> observeDiscoverShows()
        query.trim().length >= 3 -> _state.update {
          SearchResultAvailable(
              isUpdating = true,
              result = (it as? SearchResultAvailable)?.result ?: persistentListOf(),
          )
        }
        else -> Unit // Do nothing for short queries
      }
    }

    private fun handleErrorState(error: Failure) {
      _state.update {
        ErrorState(errorMessage = error.errorMessage ?: "An unknown error occurred")
      }
    }

    private fun handleSearchResults(shows: List<ShowEntity>?) {
      val state = when {
        shows.isNullOrEmpty() -> EmptyState
        else -> SearchResultAvailable(
          isUpdating = false,
          result = shows.toShowList(),
        )
      }
      _state.update { state }
    }
  }
}
