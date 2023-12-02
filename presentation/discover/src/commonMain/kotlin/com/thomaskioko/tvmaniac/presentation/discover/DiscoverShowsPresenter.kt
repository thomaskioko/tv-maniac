package com.thomaskioko.tvmaniac.presentation.discover

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.showimages.api.ShowImagesRepository
import com.thomaskioko.tvmaniac.shows.api.DiscoverRepository
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    dispatchersProvider: AppCoroutineDispatchers,
    @Assisted componentContext: ComponentContext,
    @Assisted private val onNavigateToShowDetails: (Long) -> Unit,
    @Assisted private val onNavigateToMore: (Long) -> Unit,
    private val discoverRepository: DiscoverRepository,
    private val showImagesRepository: ShowImagesRepository,
) : ComponentContext by componentContext {

    private val coroutineScope = CoroutineScope(SupervisorJob() + dispatchersProvider.main)

    private val _state = MutableStateFlow<DiscoverState>(Loading)
    val state: StateFlow<DiscoverState> = _state.asStateFlow()

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
        val recommendedResponse = discoverRepository.fetchShows(Category.RECOMMENDED)
        val popularResponse = discoverRepository.fetchShows(Category.POPULAR)
        val anticipatedResponse = discoverRepository.fetchShows(Category.ANTICIPATED)

        _state.update {
            DataLoaded(
                trendingShows = trendingResponse.toTvShowList(),
                popularShows = popularResponse.toTvShowList(),
                anticipatedShows = anticipatedResponse.toTvShowList(),
                recommendedShows = recommendedResponse.take(5).toTvShowList(),
            )
        }
    }

    private suspend fun reloadCategory(categoryId: Long) {
        // TODO:: Implementation
    }

    private suspend fun observeShowData() {
        combine(
            discoverRepository.observeShowCategory(Category.TRENDING),
            discoverRepository.observeShowCategory(Category.POPULAR),
            discoverRepository.observeShowCategory(Category.ANTICIPATED),
            discoverRepository.observeShowCategory(Category.RECOMMENDED),
            showImagesRepository.updateShowArtWork(),
        ) { trending, popular, anticipated, recommended, _ ->
            DataLoaded(
                trendingShows = trending.getOrNull().toTvShowList(),
                popularShows = popular.getOrNull().toTvShowList(),
                anticipatedShows = anticipated.getOrNull().toTvShowList(),
                recommendedShows = recommended.getOrNull()?.take(5).toTvShowList(),
                errorMessage = getErrorMessage(trending, popular, anticipated, recommended),
            )
        }
            .catch { ErrorState(errorMessage = it.message) }
            .collectLatest {
                _state.update { state ->
                    (state as? DataLoaded)?.copy(
                        errorMessage = it.errorMessage,
                        trendingShows = it.trendingShows,
                        popularShows = it.popularShows,
                        anticipatedShows = it.anticipatedShows,
                        recommendedShows = it.recommendedShows,
                    ) ?: state
                }
            }
    }
}
