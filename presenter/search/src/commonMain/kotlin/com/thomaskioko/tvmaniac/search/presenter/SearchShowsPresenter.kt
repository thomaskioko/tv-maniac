package com.thomaskioko.tvmaniac.search.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.genre.GenreRepository
import com.thomaskioko.tvmaniac.search.api.SearchRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
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

@Inject
class SearchShowsPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted("toShowDetails") val onNavigateToShowDetails: (Long) -> Unit,
    @Assisted("toGenre") val onNavigateToGenre: (Long) -> Unit,
    private val mapper: Mapper,
    private val searchRepository: SearchRepository,
    private val genreRepository: GenreRepository,
) : ComponentContext by componentContext {
    private val coroutineScope = componentContext.coroutineScope()

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
                ReloadShowContent -> coroutineScope.launch { genreRepository.fetchGenresWithShows(true) }
                LoadDiscoverShows, ClearQuery -> coroutineScope.launch { observeGenre() }
                is QueryChanged -> handleQueryChange(action.query)
                is SearchShowClicked -> onNavigateToShowDetails(action.id)
                is GenreCategoryClicked -> onNavigateToGenre(action.id)
            }
        }

        private suspend fun observeGenre() {
            genreRepository.observeGenresWithShows()
                .onStart { updateShowState() }
                .collect { result ->
                    _state.update {
                        ShowContentAvailable(
                            isUpdating = false,
                            genres = mapper.toGenreList(result),
                        )
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
                .onEach { query ->
                    updateSearchLoadingState(query)
                }
                .flatMapLatest { query -> searchRepository.observeSearchResults(query) }
                .catch { error ->
                    _state.update {
                        EmptySearchResult(
                            query = queryFlow.replayCache.lastOrNull(),
                            errorMessage = error.message ?: "An unknown error occurred",
                        )
                    }
                }
                .collect { result ->
                    handleSearchResults(result)
                }
        }

        private suspend fun updateSearchLoadingState(query: String) {
            searchRepository.search(query)
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

        private fun handleSearchResults(shows: List<ShowEntity>) {
            _state.update { state ->
                val currentQuery = queryFlow.replayCache.lastOrNull() ?: state.query
                when {
                    !state.isUpdating && shows.isEmpty() -> EmptySearchResult(
                        query = currentQuery,
                    )
                    state is SearchResultAvailable -> state.copy(
                        isUpdating = false,
                        results = mapper.toShowList(shows),
                        query = currentQuery,
                    )
                    else -> SearchResultAvailable(
                        isUpdating = false,
                        results = mapper.toShowList(shows),
                        query = currentQuery,
                    )
                }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            @Assisted componentContext: ComponentContext,
            @Assisted("toShowDetails") onNavigateToShowDetails: (Long) -> Unit,
            @Assisted("toGenre") onNavigateToGenre: (Long) -> Unit,
        ): SearchShowsPresenter
    }
}
