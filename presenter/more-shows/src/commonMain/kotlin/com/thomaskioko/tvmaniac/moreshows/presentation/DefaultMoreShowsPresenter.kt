package com.thomaskioko.tvmaniac.moreshows.presentation

import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import androidx.paging.cachedIn
import androidx.paging.map
import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.Category.POPULAR
import com.thomaskioko.tvmaniac.shows.api.model.Category.TOP_RATED
import com.thomaskioko.tvmaniac.shows.api.model.Category.TRENDING_TODAY
import com.thomaskioko.tvmaniac.shows.api.model.Category.UPCOMING
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(ActivityScope::class, MoreShowsPresenter::class)
public class DefaultMoreShowsPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val categoryId: Long,
    @Assisted private val onBack: () -> Unit,
    @Assisted private val onNavigateToShowDetails: (Long) -> Unit,
    private val popularShowsRepository: PopularShowsRepository,
    private val upcomingShowsRepository: UpcomingShowsRepository,
    private val trendingShowsRepository: TrendingShowsRepository,
    private val topRatedShowsRepository: TopRatedShowsRepository,
) : MoreShowsPresenter, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow(MoreShowsState())

    private val showsPagingDataPresenter = object : PagingDataPresenter<TvShow>() {
        override suspend fun presentPagingDataEvent(event: PagingDataEvent<TvShow>) {
            updateItemsFromSnapshot()
        }
    }

    init {
        when (categoryId) {
            UPCOMING.id -> getUpcomingPagedList()
            TRENDING_TODAY.id -> getTrendingPagedList()
            POPULAR.id -> getPopularPagedList()
            TOP_RATED.id -> getTopRatedPagedList()
        }
        observeLoadStates()
    }

    override val state: StateFlow<MoreShowsState> = _state.asStateFlow()

    override fun dispatch(action: MoreShowsActions) {
        when (action) {
            is MoreShowClicked -> onNavigateToShowDetails(action.traktId)
            MoreBackClicked -> onBack()
            RefreshMoreShows -> {
                when (categoryId) {
                    UPCOMING.id -> getUpcomingPagedList(forceRefresh = true)
                    TRENDING_TODAY.id -> getTrendingPagedList(forceRefresh = true)
                    POPULAR.id -> getPopularPagedList(forceRefresh = true)
                    TOP_RATED.id -> getTopRatedPagedList(forceRefresh = true)
                }
            }
            RetryLoadMore -> showsPagingDataPresenter.retry()
            DismissErrorMessage -> _state.update { it.copy(errorMessage = null) }
        }
    }

    override fun onItemVisible(index: Int) {
        showsPagingDataPresenter[index]
    }

    override fun loadMore() {
        val index = showsPagingDataPresenter.size - 1
        showsPagingDataPresenter[index]
    }

    private fun getPopularPagedList(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            val pagingList: Flow<PagingData<TvShow>> =
                popularShowsRepository
                    .getPagedPopularShows(forceRefresh)
                    .mapToTvShow()
                    .cachedIn(coroutineScope)

            updateState(pagingList = pagingList, title = POPULAR.title)
        }
    }

    private fun getUpcomingPagedList(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            val pagingList: Flow<PagingData<TvShow>> =
                upcomingShowsRepository
                    .getPagedUpcomingShows(forceRefresh)
                    .mapToTvShow()
                    .cachedIn(coroutineScope)

            updateState(pagingList = pagingList, title = UPCOMING.title)
        }
    }

    private fun getTrendingPagedList(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            val pagingList: Flow<PagingData<TvShow>> =
                trendingShowsRepository
                    .getPagedTrendingShows(forceRefresh)
                    .mapToTvShow()
                    .cachedIn(coroutineScope)

            updateState(pagingList = pagingList, title = TRENDING_TODAY.title)
        }
    }

    private fun getTopRatedPagedList(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            val pagingList: Flow<PagingData<TvShow>> =
                topRatedShowsRepository
                    .getPagedTopRatedShows(forceRefresh)
                    .mapToTvShow()
                    .cachedIn(coroutineScope)

            updateState(pagingList = pagingList, title = TOP_RATED.title)
        }
    }

    private suspend fun updateState(title: String, pagingList: Flow<PagingData<TvShow>>) {
        _state.update {
            it.copy(
                pagingDataFlow = pagingList,
                categoryTitle = title,
            )
        }

        pagingList.collectLatest { showsPagingDataPresenter.collectFrom(it) }
    }

    /**
     * Syncs the UI item list from the [PagingDataPresenter] snapshot.
     *
     * When the paging library's underlying [PagingSource] is invalidated (e.g., by a SQLDelight
     * query listener firing after a DB write), it recreates the source from scratch and loads
     * only `initialLoadSize` items. This causes the snapshot to temporarily shrink (e.g., from
     * 40 items back to 20).
     *
     * To prevent the UI from flickering or losing scroll position, this function:
     * 1. **Ignores shrinking snapshots** — keeps the current (larger) item list in state.
     * 2. **Re-pokes the presenter** — accesses the last item in the shrunk snapshot, which sends
     *    a [ViewportHint] to the paging library, prompting it to load the remaining pages from
     *    the database and restore the full list.
     */
    private fun updateItemsFromSnapshot() {
        val newItems = showsPagingDataPresenter.snapshot().filterNotNull().toImmutableList()
        _state.update { current ->
            when {
                current.items == newItems -> current
                newItems.size < current.items.size -> {
                    if (newItems.isNotEmpty()) {
                        showsPagingDataPresenter[newItems.size - 1]
                    }
                    current
                }
                else -> current.copy(items = newItems)
            }
        }
    }

    private fun observeLoadStates() {
        coroutineScope.launch {
            showsPagingDataPresenter.loadStateFlow.collectLatest { loadStates ->
                loadStates ?: return@collectLatest
                _state.update {
                    it.copy(
                        isRefreshLoading = loadStates.refresh is LoadState.Loading,
                        isAppendLoading = loadStates.append is LoadState.Loading,
                        appendError = (loadStates.append as? LoadState.Error)?.error?.message,
                        errorMessage = (loadStates.refresh as? LoadState.Error)?.error?.message,
                    )
                }
            }
        }
    }

    private fun Flow<PagingData<ShowEntity>>.mapToTvShow(): Flow<PagingData<TvShow>> = map {
        it.map { show ->
            TvShow(
                tmdbId = show.tmdbId,
                traktId = show.traktId,
                title = show.title,
                posterImageUrl = show.posterPath,
                inLibrary = show.inLibrary,
            )
        }
    }
}

@Inject
@ContributesBinding(ActivityScope::class, MoreShowsPresenter.Factory::class)
public class DefaultMoreShowsPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        id: Long,
        onBack: () -> Unit,
        onNavigateToShowDetails: (id: Long) -> Unit,
    ) -> MoreShowsPresenter,
) : MoreShowsPresenter.Factory {

    override fun invoke(
        componentContext: ComponentContext,
        id: Long,
        onBack: () -> Unit,
        onNavigateToShowDetails: (id: Long) -> Unit,
    ): MoreShowsPresenter = presenter(
        componentContext,
        id,
        onBack,
        onNavigateToShowDetails,
    )
}
