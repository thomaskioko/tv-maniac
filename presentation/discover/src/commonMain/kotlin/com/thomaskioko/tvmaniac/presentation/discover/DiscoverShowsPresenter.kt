package com.thomaskioko.tvmaniac.presentation.discover

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.shows.api.Category
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import com.thomaskioko.tvmaniac.util.decompose.asValue
import com.thomaskioko.tvmaniac.util.decompose.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias DiscoverShowsPresenterFactory = (
    ComponentContext,
    onNavigateToShowDetails: (id: Long) -> Unit,
    onNavigateToMore: (categoryId: Long) -> Unit,
) -> DiscoverShowsPresenter

@Inject
class DiscoverShowsPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onNavigateToShowDetails: (Long) -> Unit,
    @Assisted private val onNavigateToMore: (Long) -> Unit,
    private val featuredShowsRepository: FeaturedShowsRepository,
    private val trendingShowsRepository: TrendingShowsRepository,
    private val upcomingShowsRepository: UpcomingShowsRepository,
    private val topRatedShowsRepository: TopRatedShowsRepository,
    private val popularShowsRepository: PopularShowsRepository,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val _state = MutableStateFlow<DiscoverState>(Loading)
    val state: StateFlow<DiscoverState> = _state.asStateFlow()

    // TODO:: Create SwiftUI flow wrapper and get rid of this.
    val value: Value<DiscoverState> = _state
        .asValue(initialValue = _state.value, lifecycle = lifecycle)

    init {
        fetchContent()
    }

    fun dispatch(action: DiscoverShowAction) {
        when (action) {
            is ShowClicked -> onNavigateToShowDetails(action.id)
            PopularClicked -> onNavigateToMore(Category.POPULAR.id)
            TopRatedClicked -> onNavigateToMore(Category.TOP_RATED.id)
            TrendingClicked -> onNavigateToMore(Category.TRENDING_TODAY.id)
            UpComingClicked -> onNavigateToMore(Category.UPCOMING.id)
            RefreshData -> coroutineScope.launch {
                _state.update {
                    (it as? DataLoaded)?.copy(isRefreshing = true) ?: it
                }
                fetchShowData(true)
            }

            ReloadData -> coroutineScope.launch {
                _state.update { Loading }
                fetchShowData(true)
            }

            SnackBarDismissed -> coroutineScope.launch {
                _state.update { state ->
                    (state as? DataLoaded)?.copy(
                        errorMessage = null,
                    ) ?: state
                }
            }
        }
    }

    private fun fetchContent() {
        coroutineScope.launch {
            fetchShowData()
            observeShowData()
        }
    }

    private suspend fun fetchShowData(refresh: Boolean = false) {
        val featuredResponse = featuredShowsRepository.fetchFeaturedShows(forceRefresh = refresh)
        val topRatedResponse = topRatedShowsRepository.fetchTopRatedShows(forceRefresh = refresh)
        val popularResponse = popularShowsRepository.fetchPopularShows(forceRefresh = refresh)
        val upcomingResponse = upcomingShowsRepository.fetchUpcomingShows(forceRefresh = refresh)
        val trendingShows = trendingShowsRepository.fetchTrendingShows(forceRefresh = refresh)

        if (isEmpty(featuredResponse, topRatedResponse, popularResponse, upcomingResponse, trendingShows)) {
            return _state.update { EmptyState }
        }
        _state.update {
            DataLoaded(
                topRatedShows = topRatedResponse.toShowList(),
                popularShows = popularResponse.toShowList(),
                upcomingShows = upcomingResponse.toShowList(),
                featuredShows = featuredResponse.toShowList(),
                trendingToday = trendingShows.toShowList(),
                isRefreshing = false,
            )
        }
    }

    private fun isEmpty(vararg responses: List<ShowEntity>): Boolean {
        return responses.all { it.isEmpty() }
    }

    private suspend fun observeShowData() {
        combine(
            featuredShowsRepository.observeFeaturedShows(),
            topRatedShowsRepository.observeTopRatedShows(),
            popularShowsRepository.observePopularShows(),
            upcomingShowsRepository.observeUpcomingShows(),
            trendingShowsRepository.observeTrendingShows(),
        ) { featured, topRated, popular, upcomingShows, trendingToday ->
            _state.update {
                DataLoaded(
                    featuredShows = featured.getOrNull().toShowList(),
                    topRatedShows = topRated.getOrNull().toShowList(),
                    popularShows = popular.getOrNull().toShowList(),
                    upcomingShows = upcomingShows.getOrNull().toShowList(),
                    trendingToday = trendingToday.getOrNull().toShowList(),
                    errorMessage = getErrorMessage(topRated, popular, upcomingShows, featured),
                )
            }
        }
            .onStart { Loading }
            .catch { ErrorState(errorMessage = it.message) }
            .collect()
    }
}
