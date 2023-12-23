package com.thomaskioko.tvmaniac.presentation.discover

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import com.thomaskioko.tvmaniac.util.decompose.asValue
import com.thomaskioko.tvmaniac.util.decompose.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
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
    val state: Value<DiscoverState> = _state
        .asValue(initialValue = _state.value, lifecycle = lifecycle)

    init {
        coroutineScope.launch {
            fetchShowData()
            observeShowData()
        }
    }

    fun dispatch(action: DiscoverShowAction) {
        when (action) {
            is LoadCategoryShows -> onNavigateToMore(action.id)
            is ShowClicked -> onNavigateToShowDetails(action.id)
            is ReloadCategory -> coroutineScope.launch { reloadCategory(action.categoryId) }
            RetryLoading -> coroutineScope.launch { fetchShowData() }
            SnackBarDismissed -> coroutineScope.launch {
                _state.update { state ->
                    (state as? DataLoaded)?.copy(
                        errorMessage = null,
                    ) ?: state
                }
            }
        }
    }

    private suspend fun fetchShowData() {
        val topRatedResponse = topRatedShowsRepository.fetchTopRatedShows()
        val popularResponse = popularShowsRepository.fetchPopularShows()
        val upcomingResponse = upcomingShowsRepository.fetchUpcomingShows()
        val featuredResponse = featuredShowsRepository.fetchFeaturedTrendingShows()
        val trendingShows = trendingShowsRepository.fetchTrendingShows()

        _state.update {
            DataLoaded(
                topRatedShows = topRatedResponse.toShowList(),
                popularShows = popularResponse.toShowList(),
                upcomingShows = upcomingResponse.toShowList(),
                featuredShows = featuredResponse.toShowList(),
                trendingToday = trendingShows.toShowList(),
            )
        }
    }

    private fun reloadCategory(categoryId: Long) {
        // TODO:: Implementation
    }

    private suspend fun observeShowData() {
        combine(
            featuredShowsRepository.observeFeaturedShows(),
            topRatedShowsRepository.observeTopRatedShows(),
            popularShowsRepository.observePopularShows(),
            upcomingShowsRepository.observeUpcomingShows(),
            trendingShowsRepository.observeTrendingShows(),
        ) { featured, trending, popular, upcomingShows, trendingToday ->
            DataLoaded(
                featuredShows = featured.getOrNull().toShowList(),
                topRatedShows = trending.getOrNull().toShowList(),
                popularShows = popular.getOrNull().toShowList(),
                upcomingShows = upcomingShows.getOrNull().toShowList(),
                trendingToday = trendingToday.getOrNull().toShowList(),
                errorMessage = getErrorMessage(trending, popular, upcomingShows, featured),
            )
        }
            .catch { ErrorState(errorMessage = it.message) }
            .collectLatest {
                _state.update { state ->
                    (state as? DataLoaded)?.copy(
                        errorMessage = it.errorMessage,
                        topRatedShows = it.topRatedShows,
                        popularShows = it.popularShows,
                        upcomingShows = it.upcomingShows,
                        featuredShows = it.featuredShows,
                        trendingToday = it.trendingToday,
                    ) ?: state
                }
            }
    }
}
