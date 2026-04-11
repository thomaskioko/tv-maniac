package com.thomaskioko.tvmaniac.presentation.library

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.data.library.model.LibraryItem
import com.thomaskioko.tvmaniac.data.library.model.WatchProvider
import com.thomaskioko.tvmaniac.domain.library.ObserveLibraryInteractor
import com.thomaskioko.tvmaniac.domain.library.SyncLibraryInteractor
import com.thomaskioko.tvmaniac.presentation.library.model.LibraryShowItem
import com.thomaskioko.tvmaniac.presentation.library.model.LibrarySortOption
import com.thomaskioko.tvmaniac.presentation.library.model.ShowStatus
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.thomaskioko.tvmaniac.data.library.model.LibrarySortOption as DataLibrarySortOption

@AssistedInject
public class LibraryPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigateToShowDetails: (id: Long) -> Unit,
    private val repository: LibraryRepository,
    private val observeLibraryInteractor: ObserveLibraryInteractor,
    private val syncLibraryInteractor: SyncLibraryInteractor,
    private val traktAuthRepository: TraktAuthRepository,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val uiMessageManager = UiMessageManager()
    private val loadingState = ObservableLoadingCounter()
    private val coroutineScope = coroutineScope()
    private val queryFlow = MutableStateFlow("")
    private val sortOptionFlow = MutableStateFlow(LibrarySortOption.ADDED_DESC)
    private val followedOnlyFlow = MutableStateFlow(false)
    private val selectedGenresFlow = MutableStateFlow<Set<String>>(emptySet())
    private val selectedStatusesFlow = MutableStateFlow<Set<ShowStatus>>(emptySet())
    private val _state = MutableStateFlow(LibraryState())

    init {
        observeSortOptionChanges()
        observeAuthState()
        observeLibrary()
    }

    public val state: StateFlow<LibraryState> = combine(
        _state,
        observeLibraryInteractor.flow,
        repository.observeListStyle(),
        repository.observeSortOption().map { it.toPresentation() },
        uiMessageManager.message,
        queryFlow,
        followedOnlyFlow,
        selectedGenresFlow,
        selectedStatusesFlow,
        loadingState.observable,
    ) { currentState, items, isGridMode, sortOption, message, query, followedOnly, selectedGenres,
        selectedStatuses, isLoading,
        ->

        val availableGenres = getAvailableGenres(items)
        val availableStatuses = getAvailableStatuses(items)
        val filteredItems = applyFilters(items, selectedGenres, selectedStatuses)
        val sortedItems = applySorting(filteredItems, sortOption)

        currentState.copy(
            query = query,
            isGridMode = isGridMode,
            isRefreshing = isLoading,
            sortOption = sortOption,
            followedOnly = followedOnly,
            availableGenres = availableGenres.toImmutableList(),
            selectedGenres = selectedGenres.toImmutableSet(),
            availableStatuses = availableStatuses.toImmutableList(),
            selectedStatuses = selectedStatuses.toImmutableSet(),
            items = sortedItems.map { it.toLibraryShowItem() }.toImmutableList(),
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LibraryState(),
    )

    public val stateValue: Value<LibraryState> = state.asValue(coroutineScope)

    public fun dispatch(action: LibraryAction) {
        when (action) {
            is LibraryShowClicked -> navigateToShowDetails(action.traktId)
            is LibraryQueryChanged -> updateQuery(action.query)
            is ClearLibraryQuery -> clearQuery()
            is ToggleSearchActive -> toggleSearchActive()
            is ChangeListStyleClicked -> toggleListStyle(action.isGridMode)
            is ChangeSortOption -> changeSortOption(action.sortOption)
            is ToggleGenreFilter -> toggleGenreFilter(action.genre)
            is ToggleStatusFilter -> toggleStatusFilter(action.status)
            is ClearFilters -> clearFilters()
            is MessageShown -> clearMessage(action.id)
            is RefreshLibrary -> syncLibrary(forceRefresh = true)
        }
    }

    private fun getAvailableGenres(items: List<LibraryItem>): List<String> {
        return items
            .flatMap { it.genres.orEmpty() }
            .distinct()
            .sorted()
    }

    private fun getAvailableStatuses(items: List<LibraryItem>): List<ShowStatus> {
        return items
            .mapNotNull { item -> ShowStatus.fromDisplayName(item.status) }
            .distinct()
            .sortedBy { it.ordinal }
    }

    private fun applyFilters(
        items: List<LibraryItem>,
        selectedGenres: Set<String>,
        selectedStatuses: Set<ShowStatus>,
    ): List<LibraryItem> {
        return items.filter { item ->
            val matchesGenre = selectedGenres.isEmpty() ||
                item.genres?.any { it in selectedGenres } == true
            val matchesStatus = selectedStatuses.isEmpty() ||
                ShowStatus.fromDisplayName(item.status) in selectedStatuses
            matchesGenre && matchesStatus
        }
    }

    private fun applySorting(
        items: List<LibraryItem>,
        sortOption: LibrarySortOption,
    ): List<LibraryItem> {
        return when (sortOption) {
            LibrarySortOption.RANK_ASC -> items
            LibrarySortOption.RANK_DESC -> items.reversed()
            LibrarySortOption.ADDED_DESC -> items.sortedByDescending { it.followedAt ?: 0L }
            LibrarySortOption.ADDED_ASC -> items.sortedBy { it.followedAt ?: Long.MAX_VALUE }
            LibrarySortOption.RELEASED_DESC -> items.sortedByDescending { it.year }
            LibrarySortOption.RELEASED_ASC -> items.sortedBy { it.year }
            LibrarySortOption.TITLE_ASC -> items.sortedBy { it.title.lowercase() }
            LibrarySortOption.TITLE_DESC -> items.sortedByDescending { it.title.lowercase() }
        }
    }

    private fun observeAuthState() {
        coroutineScope.launch {
            traktAuthRepository.state
                .distinctUntilChanged()
                .filter { it == TraktAuthState.LOGGED_IN }
                .collect { syncLibrary() }
        }
    }

    private fun syncLibrary(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            syncLibraryInteractor(SyncLibraryInteractor.Param(forceRefresh = forceRefresh))
                .collectStatus(loadingState, logger, uiMessageManager, "Library", errorToStringMapper)
        }
    }

    private fun observeSortOptionChanges() {
        coroutineScope.launch {
            repository.observeSortOption().collect { sortOption ->
                val presentationSortOption = sortOption.toPresentation()
                if (sortOptionFlow.value != presentationSortOption) {
                    sortOptionFlow.value = presentationSortOption
                    observeLibrary()
                }
            }
        }
    }

    private fun observeLibrary() {
        observeLibraryInteractor(
            ObserveLibraryInteractor.Params(
                query = queryFlow.value,
                sortOption = sortOptionFlow.value.toData(),
                followedOnly = followedOnlyFlow.value,
            ),
        )
    }

    private fun updateQuery(query: String) {
        coroutineScope.launch {
            queryFlow.emit(query)
            observeLibrary()
        }
    }

    private fun clearQuery() {
        coroutineScope.launch {
            queryFlow.emit("")
            observeLibrary()
        }
    }

    private fun toggleSearchActive() {
        _state.update { it.copy(isSearchActive = !it.isSearchActive) }
    }

    private fun toggleListStyle(currentIsGridMode: Boolean) {
        coroutineScope.launch {
            repository.saveListStyle(!currentIsGridMode)
        }
    }

    private fun changeSortOption(sortOption: LibrarySortOption) {
        coroutineScope.launch {
            sortOptionFlow.emit(sortOption)
            repository.saveSortOption(sortOption.toData())
        }
    }

    private fun toggleGenreFilter(genre: String) {
        coroutineScope.launch {
            val currentGenres = selectedGenresFlow.value
            val newGenres = if (genre in currentGenres) {
                currentGenres - genre
            } else {
                currentGenres + genre
            }
            selectedGenresFlow.emit(newGenres)
        }
    }

    private fun toggleStatusFilter(status: ShowStatus) {
        coroutineScope.launch {
            val currentStatuses = selectedStatusesFlow.value
            val newStatuses = if (status in currentStatuses) {
                currentStatuses - status
            } else {
                currentStatuses + status
            }
            selectedStatusesFlow.emit(newStatuses)
        }
    }

    private fun clearFilters() {
        coroutineScope.launch {
            selectedGenresFlow.emit(emptySet())
            selectedStatusesFlow.emit(emptySet())
            sortOptionFlow.emit(LibrarySortOption.ADDED_DESC)
            repository.saveSortOption(DataLibrarySortOption.ADDED_DESC)
        }
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(
            componentContext: ComponentContext,
            navigateToShowDetails: (id: Long) -> Unit,
        ): LibraryPresenter
    }
}

private fun LibraryItem.toLibraryShowItem(): LibraryShowItem = LibraryShowItem(
    traktId = traktId,
    tmdbId = tmdbId,
    title = title,
    posterImageUrl = posterPath,
    status = status,
    year = year,
    rating = rating,
    genres = genres,
    seasonCount = seasonCount,
    episodeCount = episodeCount,
    isFollowed = isFollowed,
    watchProviders = watchProviders.map { it.toUiModel() }.toImmutableList(),
)

private fun WatchProvider.toUiModel() =
    com.thomaskioko.tvmaniac.presentation.library.model.WatchProviderUiModel(
        id = id,
        name = name,
        logoUrl = logoPath,
    )

private fun LibrarySortOption.toData(): DataLibrarySortOption = when (this) {
    LibrarySortOption.RANK_ASC -> DataLibrarySortOption.RANK_ASC
    LibrarySortOption.RANK_DESC -> DataLibrarySortOption.RANK_DESC
    LibrarySortOption.ADDED_DESC -> DataLibrarySortOption.ADDED_DESC
    LibrarySortOption.ADDED_ASC -> DataLibrarySortOption.ADDED_ASC
    LibrarySortOption.RELEASED_DESC -> DataLibrarySortOption.RELEASED_DESC
    LibrarySortOption.RELEASED_ASC -> DataLibrarySortOption.RELEASED_ASC
    LibrarySortOption.TITLE_ASC -> DataLibrarySortOption.TITLE_ASC
    LibrarySortOption.TITLE_DESC -> DataLibrarySortOption.TITLE_DESC
}

private fun DataLibrarySortOption.toPresentation(): LibrarySortOption = when (this) {
    DataLibrarySortOption.RANK_ASC -> LibrarySortOption.RANK_ASC
    DataLibrarySortOption.RANK_DESC -> LibrarySortOption.RANK_DESC
    DataLibrarySortOption.ADDED_DESC -> LibrarySortOption.ADDED_DESC
    DataLibrarySortOption.ADDED_ASC -> LibrarySortOption.ADDED_ASC
    DataLibrarySortOption.RELEASED_DESC -> LibrarySortOption.RELEASED_DESC
    DataLibrarySortOption.RELEASED_ASC -> LibrarySortOption.RELEASED_ASC
    DataLibrarySortOption.TITLE_ASC -> LibrarySortOption.TITLE_ASC
    DataLibrarySortOption.TITLE_DESC -> LibrarySortOption.TITLE_DESC
}
