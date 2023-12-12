package com.thomaskioko.tvmaniac.presentation.discover

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.shows.api.DiscoverRepository
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
    private val discoverRepository: DiscoverRepository,
    private val trendingShowsRepository: TrendingShowsRepository,
    private val upcomingShowsRepository: UpcomingShowsRepository,
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
        val trendingResponse = discoverRepository.fetchShows(Category.TRENDING)
        val popularResponse = discoverRepository.fetchShows(Category.POPULAR)
        val upcomingResponse = upcomingShowsRepository.fetchUpcomingShows()
        val featuredResponse = trendingShowsRepository.fetchFeaturedTrendingShows()
        val trendingShows = trendingShowsRepository.fetchTrendingShows()

        _state.update {
            DataLoaded(
                trendingShows = trendingResponse.toTvShowList(),
                popularShows = popularResponse.toTvShowList(),
                upcomingShows = upcomingResponse.toUpcomingShowList(),
                featuredShows = featuredResponse.toDiscoverShowList(),
                trendingToday = trendingShows.toDiscoverShowList(),
            )
        }
    }

    private suspend fun reloadCategory(categoryId: Long) {
        // TODO:: Implementation
    }

    private suspend fun observeShowData() {
        combine(
            trendingShowsRepository.observeFeaturedTrendingShows(),
            discoverRepository.observeShowCategory(Category.TRENDING),
            discoverRepository.observeShowCategory(Category.POPULAR),
            upcomingShowsRepository.observeUpcomingShows(),
            trendingShowsRepository.observeTrendingShows(),
        ) { recommended, trending, popular, upcomingShows, trendingToday ->
            DataLoaded(
                featuredShows = recommended.getOrNull().toDiscoverShowList(),
                trendingShows = trending.getOrNull().toTvShowList(),
                popularShows = popular.getOrNull().toTvShowList(),
                upcomingShows = upcomingShows.getOrNull().toUpcomingShowList(),
                trendingToday = trendingToday.getOrNull().toDiscoverShowList(),
                errorMessage = getErrorMessage(trending, popular, upcomingShows, recommended),
            )
        }
            .catch { ErrorState(errorMessage = it.message) }
            .collectLatest {
                _state.update { state ->
                    (state as? DataLoaded)?.copy(
                        errorMessage = it.errorMessage,
                        trendingShows = it.trendingShows,
                        popularShows = it.popularShows,
                        upcomingShows = it.upcomingShows,
                        featuredShows = it.featuredShows,
                        trendingToday = it.trendingToday,
                    ) ?: state
                }
            }
    }
}
