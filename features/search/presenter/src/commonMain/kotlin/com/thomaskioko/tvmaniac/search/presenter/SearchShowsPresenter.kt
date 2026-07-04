package com.thomaskioko.tvmaniac.search.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.genre.FetchGenreContentInteractor
import com.thomaskioko.tvmaniac.genre.GenreRepository
import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.search.api.SearchRepository
import com.thomaskioko.tvmaniac.search.nav.SearchRoute
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SEARCH_SOURCE_ID = "Search"

private fun String.isSearchable(): Boolean = trim().length >= SearchShowState.SEARCH_QUERY_LENGTH

@NavDestination(
    route = SearchRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@Inject
public class SearchShowsPresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val mapper: Mapper,
    private val searchRepository: SearchRepository,
    private val genreRepository: GenreRepository,
    private val fetchGenreContentInteractor: FetchGenreContentInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val presenterInstance = instanceKeeper.getOrCreate { PresenterInstance() }
    public val state: StateFlow<SearchShowState> = presenterInstance.state

    public val stateValue: Value<SearchShowState> = state.asValue(coroutineScope)

    init {
        presenterInstance.init()
    }

    public fun dispatch(action: SearchShowAction) {
        presenterInstance.dispatch(action)
    }

    internal inner class PresenterInstance : InstanceKeeper.Instance {
        private var isInitialized = false
        private val genreLoadingState = ObservableLoadingCounter()
        private val uiMessageManager = UiMessageManager()
        private val _state: MutableStateFlow<SearchShowState> =
            MutableStateFlow(SearchShowState.Empty)
        val state: StateFlow<SearchShowState> = combine(
            genreRepository.observeGenresWithShowRows(),
            genreRepository.observeGenreShowCategory(),
            genreLoadingState.observable,
            uiMessageManager.message,
            _state,
        ) { result, category, isLoading, message, currentState ->
            currentState.copy(
                isRefreshing = isLoading,
                message = message,
                genreRows = mapper.toGenreRows(result),
                selectedCategory = category,
                categoryTitle = mapper.categoryTitle(),
                categories = mapper.toCategoryItems(),
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
                launch { observeCategoryChanges() }
                launch { observeLocalResults() }
                launch { fetchSearchQuery() }
            }
        }

        fun dispatch(action: SearchShowAction) {
            when (action) {
                BackClicked -> navigator.navigateBack()

                is MessageShown -> {
                    coroutineScope.launch { uiMessageManager.clearMessage(action.id) }
                }

                ReloadShowContent -> {
                    fetchGenreContent(
                        category = state.value.selectedCategory,
                        forceRefresh = true,
                    )
                }

                ClearQuery -> {
                    coroutineScope.launch { resetSearch() }
                }

                is CategoryChanged -> {
                    coroutineScope.launch { genreRepository.saveGenreShowCategory(action.category) }
                }

                is QueryChanged -> handleQueryChange(action.query)
                is SearchShowClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
            }
        }

        private suspend fun observeCategoryChanges() {
            genreRepository.observeGenreShowCategory()
                .distinctUntilChanged()
                .collect { category ->
                    fetchGenreContent(category = category)
                }
        }

        private fun fetchGenreContent(
            category: GenreShowCategory,
            forceRefresh: Boolean = false,
        ) {
            coroutineScope.launch {
                fetchGenreContentInteractor(FetchGenreContentInteractor.Params(category, forceRefresh))
                    .collectStatus(genreLoadingState, logger, uiMessageManager, "Genre Content", errorToStringMapper)
            }
        }

        private suspend fun observeLocalResults() {
            queryFlow
                .distinctUntilChanged()
                .debounce(SearchShowState.LOCAL_SUGGESTION_DEBOUNCE)
                .flatMapLatest { query ->
                    if (query.isSearchable()) {
                        searchRepository.observeSearchResults(query)
                    } else {
                        flowOf(emptyList())
                    }
                }
                .catch { error ->
                    uiMessageManager.emitMessage(UiMessage(message = errorToStringMapper.mapError(error), sourceId = SEARCH_SOURCE_ID))
                }
                .collect { results -> handleSearchResults(results) }
        }

        private suspend fun fetchSearchQuery() {
            queryFlow
                .distinctUntilChanged()
                .debounce(SearchShowState.NETWORK_DEBOUNCE)
                .flatMapLatest { query ->
                    if (query.isSearchable()) {
                        searchNetwork(query)
                    } else {
                        emptyFlow()
                    }
                }
                .collect()
        }

        private fun searchNetwork(query: String): Flow<Unit> = flow {
            _state.update { it.copy(isUpdating = true) }
            searchRepository.search(query)
            _state.update { it.copy(isUpdating = false) }
            emit(Unit)
        }
            .catch { error ->
                _state.update { it.copy(isUpdating = false) }
                uiMessageManager.emitMessage(UiMessage(message = errorToStringMapper.mapError(error), sourceId = SEARCH_SOURCE_ID))
            }

        private fun handleQueryChange(query: String) {
            coroutineScope.launch {
                if (query.isSearchable()) {
                    _state.update { it.copy(query = query, isUpdating = true) }
                    queryFlow.emit(query)
                } else {
                    resetSearch(query)
                }
            }
        }

        private suspend fun resetSearch(query: String = "") {
            state.value.message
                ?.takeIf { it.sourceId == SEARCH_SOURCE_ID }
                ?.let { uiMessageManager.clearMessage(it.id) }
            _state.update { it.copy(query = query, isUpdating = false, searchResults = persistentListOf()) }
            queryFlow.emit(query)
        }

        private fun handleSearchResults(shows: List<ShowEntity>) {
            _state.update { it.copy(searchResults = mapper.toShowList(shows)) }
        }
    }
}
