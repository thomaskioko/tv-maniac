package com.thomaskioko.tvmaniac.discover.api

import com.thomaskioko.tvmaniac.discover.api.DiscoverShowResult.DiscoverShowsData
import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.BaseViewModel
import com.thomaskioko.tvmaniac.shared.core.ui.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DiscoverShowsViewModel : BaseViewModel(), KoinComponent {

    private val observeDiscoverShow: ObserveDiscoverShowsInteractor by inject()

    private val _uiState = MutableStateFlow<DiscoverShowsState>(DiscoverShowsState.InProgress)

    override val state: StateFlow<DiscoverShowsState> = _uiState

    override fun attach() {
        dispatch(DiscoverShowActions.LoadShow)
    }

    override fun dispatch(action: Action) {
        when (action) {
            DiscoverShowActions.LoadShow -> {
                observeDiscoverShow.invoke(Unit)
                    .onEach {
                        _uiState.value = DiscoverShowsState.Success(
                            data = DiscoverShowsState.DiscoverShowResult(
                                featuredShows = it.featuredShows,
                                trendingShows = it.trendingShows,
                                topRatedShows = it.recommendedShows,
                                popularShows = it.popularShows
                            )
                        )
                    }
                    .catch {
                        _uiState.value =
                            DiscoverShowsState.Error(it.message ?: "Something went wrong")
                    }
                    .launchIn(vmScope)
            }
        }
    }
}

sealed class DiscoverShowActions : Action {
    object LoadShow : DiscoverShowActions()
}

sealed class DiscoverShowsState : ViewState() {
    object InProgress : DiscoverShowsState()
    class Error(val error: String) : DiscoverShowsState()
    class Success(val data: DiscoverShowResult) : DiscoverShowsState()

    data class DiscoverShowResult(
        val featuredShows: DiscoverShowsData,
        val trendingShows: DiscoverShowsData,
        val topRatedShows: DiscoverShowsData,
        val popularShows: DiscoverShowsData,
    )
}
