package com.thomaskioko.tvmaniac.presentation.viewmodel

import com.thomaskioko.tvmaniac.interactor.ObserveDiscoverShowsInteractor
import com.thomaskioko.tvmaniac.presentation.contract.DiscoverShowResult.DiscoverShowsData
import com.thomaskioko.tvmaniac.presentation.viewmodel.DiscoverShowsState.Error
import com.thomaskioko.tvmaniac.presentation.viewmodel.DiscoverShowsState.Success
import com.thomaskioko.tvmaniac.shared.core.BaseViewModel
import com.thomaskioko.tvmaniac.shared.core.ViewState
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
        observeDiscoverShow.invoke(Unit)
            .onEach {
                _uiState.value = Success(
                    data = DiscoverShowsState.DiscoverShowResult(
                        featuredShows = it.featuredShows,
                        trendingShows = it.trendingShows,
                        topRatedShows = it.topRatedShows,
                        popularShows = it.popularShows
                    )
                )
            }
            .catch { _uiState.value = Error(it.message ?: "Something went wrong") }
            .launchIn(vmScope)
    }
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
