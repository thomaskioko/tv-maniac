package com.thomaskioko.tvmaniac.genreshows.presentation

import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import androidx.paging.cachedIn
import androidx.paging.map
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.genre.GenreRepository
import com.thomaskioko.tvmaniac.genreshows.nav.GenreShowsRoute
import com.thomaskioko.tvmaniac.genreshows.nav.model.GenreShowsParam
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@NavDestination(
    route = GenreShowsRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@AssistedInject
public class GenreShowsPresenter internal constructor(
    componentContext: ComponentContext,
    @Assisted private val param: GenreShowsParam,
    private val navigator: Navigator,
    private val genreRepository: GenreRepository,
    private val errorToStringMapper: ErrorToStringMapper,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow(GenreShowsState(title = param.name))

    private val pagingDataPresenter = object : PagingDataPresenter<GenreShow>() {
        override suspend fun presentPagingDataEvent(event: PagingDataEvent<GenreShow>) {
            updateItemsFromSnapshot()
        }
    }

    init {
        loadGenreShows()
        observeLoadStates()
    }

    public val state: StateFlow<GenreShowsState> = _state.asStateFlow()

    public val stateValue: Value<GenreShowsState> = state.asValue(coroutineScope)

    public fun dispatch(action: GenreShowsAction) {
        when (action) {
            is GenreShowClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
            GenreShowsBackClicked -> navigator.navigateBack()
            RefreshGenreShows -> loadGenreShows(forceRefresh = true)
            RetryGenreShowsLoadMore -> pagingDataPresenter.retry()
            DismissGenreShowsError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    public fun onItemVisible(index: Int) {
        pagingDataPresenter[index]
    }

    public fun loadMore() {
        val index = pagingDataPresenter.size - 1
        if (index >= 0) {
            pagingDataPresenter[index]
        }
    }

    private fun loadGenreShows(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            val pagingList: Flow<PagingData<GenreShow>> = genreRepository
                .getPagedGenreShows(slug = param.slug, category = param.category, forceRefresh = forceRefresh)
                .mapToGenreShow()
                .cachedIn(coroutineScope)

            _state.update { it.copy(pagingDataFlow = pagingList) }

            pagingList.collectLatest { pagingDataPresenter.collectFrom(it) }
        }
    }

    private fun updateItemsFromSnapshot() {
        val newItems = pagingDataPresenter.snapshot().filterNotNull().toImmutableList()
        _state.update { current ->
            when {
                current.items == newItems -> current
                newItems.size < current.items.size -> {
                    if (newItems.isNotEmpty()) {
                        pagingDataPresenter[newItems.size - 1]
                    }
                    current
                }
                else -> current.copy(items = newItems)
            }
        }
    }

    private fun observeLoadStates() {
        coroutineScope.launch {
            pagingDataPresenter.loadStateFlow.collectLatest { loadStates ->
                loadStates ?: return@collectLatest
                _state.update {
                    it.copy(
                        isRefreshLoading = loadStates.refresh is LoadState.Loading,
                        isAppendLoading = loadStates.append is LoadState.Loading,
                        hasNextPage = !loadStates.append.endOfPaginationReached,
                        appendError = (loadStates.append as? LoadState.Error)?.let { error -> errorToStringMapper.mapError(error.error) },
                        errorMessage = (loadStates.refresh as? LoadState.Error)?.let { error -> errorToStringMapper.mapError(error.error) },
                    )
                }
            }
        }
    }

    private fun Flow<PagingData<ShowEntity>>.mapToGenreShow(): Flow<PagingData<GenreShow>> = map { pagingData ->
        pagingData.map { show ->
            GenreShow(
                tmdbId = show.tmdbId,
                showId = show.showId,
                title = show.title,
                posterImageUrl = show.posterPath,
                inLibrary = show.inLibrary,
            )
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(param: GenreShowsParam): GenreShowsPresenter
    }
}
