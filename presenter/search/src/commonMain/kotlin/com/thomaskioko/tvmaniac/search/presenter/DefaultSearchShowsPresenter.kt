package com.thomaskioko.tvmaniac.search.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.genre.GenreRepository
import com.thomaskioko.tvmaniac.search.api.SearchRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(ActivityScope::class, SearchShowsPresenter::class)
public class DefaultSearchShowsPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onNavigateToShowDetails: (Long) -> Unit,
    @Assisted private val onNavigateToGenre: (Long) -> Unit,
    private val mapper: Mapper,
    private val searchRepository: SearchRepository,
    private val genreRepository: GenreRepository,
    private val coroutineScope: CoroutineScope = componentContext.coroutineScope(),
) : SearchShowsPresenter, ComponentContext by componentContext {

    private val presenterInstance = instanceKeeper.getOrCreate { PresenterInstance() }
    override val state: StateFlow<SearchShowState> = presenterInstance.state

    init {
        presenterInstance.init()
    }

    override fun dispatch(action: SearchShowAction) {
        presenterInstance.dispatch(action)
    }

    internal inner class PresenterInstance : InstanceKeeper.Instance {
        private var isInitialized = false
        private val _state: MutableStateFlow<SearchShowState> =
            MutableStateFlow(SearchShowState.Empty)
        val state: StateFlow<SearchShowState> = combine(
            genreRepository.observeGenresWithShows(),
            _state,
        ) { result, currentState ->
            currentState.copy(
                isUpdating = false,
                genres = mapper.toGenreList(result),
            )
        }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SearchShowState.Empty,
            )

        private val queryFlow = MutableSharedFlow<String>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
            extraBufferCapacity = 1,
        )

        fun init() {
            if (isInitialized) return
            isInitialized = true

            coroutineScope.launch {
                launch { observeGenre() }
                launch { observeQueryFlow() }
            }
        }

        fun dispatch(action: SearchShowAction) {
            when (action) {
                DismissSnackBar -> {
                    _state.update { it.copy(errorMessage = null) }
                }

                ReloadShowContent -> coroutineScope.launch {
                    genreRepository.fetchGenresWithShows(
                        true,
                    )
                }

                LoadDiscoverShows, ClearQuery -> {
                    _state.update { it.copy(query = "", searchResults = persistentListOf()) }
                }

                is QueryChanged -> handleQueryChange(action.query)
                is SearchShowClicked -> onNavigateToShowDetails(action.id)
                is GenreCategoryClicked -> onNavigateToGenre(action.id)
            }
        }

        private suspend fun observeGenre() {
            genreRepository.observeGenrePosters()
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
                        it.copy(
                            isUpdating = false,
                            errorMessage = error.message ?: "An unknown error occurred",
                        )
                    }
                }
                .collect { result ->
                    handleSearchResults(result)
                }
        }

        private suspend fun updateSearchLoadingState(query: String) {
            _state.update { it.copy(isUpdating = true, query = query) }
            searchRepository.search(query)
        }

        private fun handleQueryChange(query: String) {
            coroutineScope.launch {
                if (query.isEmpty()) {
                    _state.update { it.copy(query = "", searchResults = persistentListOf()) }
                } else {
                    queryFlow.emit(query)
                }
            }
        }

        private fun handleSearchResults(shows: List<ShowEntity>) {
            _state.update { it.copy(isUpdating = false, searchResults = mapper.toShowList(shows)) }
        }
    }
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, SearchShowsPresenter.Factory::class)
public class DefaultSearchPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        onNavigateToShowDetails: (id: Long) -> Unit,
        onNavigateToGenre: (id: Long) -> Unit,
    ) -> SearchShowsPresenter,
) : SearchShowsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        onNavigateToShowDetails: (id: Long) -> Unit,
        onNavigateToGenre: (id: Long) -> Unit,
    ): SearchShowsPresenter =
        presenter(componentContext, onNavigateToShowDetails, onNavigateToGenre)
}
