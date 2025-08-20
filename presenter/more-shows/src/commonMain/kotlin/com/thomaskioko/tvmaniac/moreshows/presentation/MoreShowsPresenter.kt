package com.thomaskioko.tvmaniac.moreshows.presentation

import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import androidx.paging.cachedIn
import androidx.paging.map
import com.arkivanov.decompose.ComponentContext
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
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
class MoreShowsPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted val categoryId: Long,
    @Assisted val onBack: () -> Unit,
    @Assisted val onNavigateToShowDetails: (Long) -> Unit,
    private val popularShowsRepository: PopularShowsRepository,
    private val upcomingShowsRepository: UpcomingShowsRepository,
    private val trendingShowsRepository: TrendingShowsRepository,
    private val topRatedShowsRepository: TopRatedShowsRepository,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow(MoreShowsState())

    private val showsPagingDataPresenter = object : PagingDataPresenter<TvShow>() {
        override suspend fun presentPagingDataEvent(event: PagingDataEvent<TvShow>) {
            updateCharactersSnapshotList()
        }
    }

    init {
        when (categoryId) {
            UPCOMING.id -> getUpcomingPagedList()
            TRENDING_TODAY.id -> getTrendingPagedList()
            POPULAR.id -> getPopularPagedList()
            TOP_RATED.id -> getTopRatedPagedList()
        }
    }

    val state: StateFlow<MoreShowsState> = _state.asStateFlow()

    fun dispatch(action: MoreShowsActions) {
        when (action) {
            is MoreShowClicked -> onNavigateToShowDetails(action.showId)
            MoreBackClicked -> onBack()
            RefreshMoreShows -> {
                when (categoryId) {
                    UPCOMING.id -> getUpcomingPagedList(forceRefresh = true)
                    TRENDING_TODAY.id -> getTrendingPagedList(forceRefresh = true)
                    POPULAR.id -> getPopularPagedList(forceRefresh = true)
                    TOP_RATED.id -> getTopRatedPagedList(forceRefresh = true)
                }
            }
        }
    }

    /** Helper method used to get a show object in iOS */
    fun getElement(index: Int): TvShow? = showsPagingDataPresenter[index]

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

    private fun updateCharactersSnapshotList() {
        _state.update {
            it.copy(
                snapshotList = showsPagingDataPresenter.snapshot(),
            )
        }
    }

    private fun Flow<PagingData<ShowEntity>>.mapToTvShow(): Flow<PagingData<TvShow>> = map {
        it.map { show ->
            TvShow(
                tmdbId = show.id,
                title = show.title,
                posterImageUrl = show.posterPath,
                inLibrary = show.inLibrary,
            )
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            categoryId: Long,
            onBack: () -> Unit,
            onNavigateToShowDetails: (Long) -> Unit,
        ): MoreShowsPresenter
    }
}
