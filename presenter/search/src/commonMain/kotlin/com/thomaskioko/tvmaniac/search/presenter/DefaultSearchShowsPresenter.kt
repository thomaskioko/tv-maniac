package com.thomaskioko.tvmaniac.search.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.genre.FetchGenreContentInteractor
import com.thomaskioko.tvmaniac.genre.GenreRepository
import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
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
    private val fetchGenreContentInteractor: FetchGenreContentInteractor,
    private val logger: Logger,
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
                launch { observeQueryFlow() }
            }
        }

        fun dispatch(action: SearchShowAction) {
            when (action) {
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
                    _state.update { it.copy(query = "", searchResults = persistentListOf()) }
                }

                is CategoryChanged -> {
                    coroutineScope.launch { genreRepository.saveGenreShowCategory(action.category) }
                }

                is QueryChanged -> handleQueryChange(action.query)
                is SearchShowClicked -> onNavigateToShowDetails(action.id)
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
                    .collectStatus(genreLoadingState, logger, uiMessageManager, "Genre Content")
            }
        }

        private suspend fun observeQueryFlow() {
            queryFlow
                .distinctUntilChanged()
                .debounce(300)
                .filter { it.trim().length >= SearchShowState.SEARCH_QUERY_LENGTH }
                .onEach { _state.update { it.copy(isUpdating = true) } }
                .flatMapLatest { query ->
                    coroutineScope.launch { searchRepository.search(query) }
                    searchRepository.observeSearchResults(query)
                }
                .catch { error ->
                    uiMessageManager.emitMessageCombined(error, "Search")
                    _state.update { it.copy(isUpdating = false) }
                }
                .collect { result ->
                    handleSearchResults(result)
                }
        }

        private fun handleQueryChange(query: String) {
            coroutineScope.launch {
                if (query.isEmpty()) {
                    _state.update { it.copy(query = "", searchResults = persistentListOf()) }
                } else {
                    val isSearchable = query.trim().length >= SearchShowState.SEARCH_QUERY_LENGTH
                    _state.update { it.copy(query = query, isUpdating = isSearchable) }
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
