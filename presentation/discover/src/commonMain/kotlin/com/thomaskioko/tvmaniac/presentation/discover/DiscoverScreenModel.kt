package com.thomaskioko.tvmaniac.presentation.discover

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.showimages.api.ShowImagesRepository
import com.thomaskioko.tvmaniac.shows.api.DiscoverRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class DiscoverScreenModel @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    private val showImagesRepository: ShowImagesRepository,
) : ScreenModel {

    private val _state = MutableStateFlow<DiscoverState>(Loading)
    val state = _state.asStateFlow()

    init {
        screenModelScope.launch {
            fetchShowData()
            observeShowData()
        }
    }

    fun dispatch(action: ShowsAction) {
        when (action) {
            is ReloadCategory -> screenModelScope.launch { reloadCategory(action.categoryId) }
            RetryLoading -> screenModelScope.launch { fetchShowData() }
            SnackBarDismissed -> screenModelScope.launch {
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
