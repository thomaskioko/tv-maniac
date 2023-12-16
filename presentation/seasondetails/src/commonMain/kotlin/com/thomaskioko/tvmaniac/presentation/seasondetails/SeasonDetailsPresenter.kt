package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.util.decompose.asValue
import com.thomaskioko.tvmaniac.util.decompose.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias SeasonDetailsPresenterFactory = (
    ComponentContext,
    param: SeasonDetailsUiParam,
    onBack: () -> Unit,
    onNavigateToEpisodeDetails: (id: Long) -> Unit,
) -> SeasonDetailsPresenter

class SeasonDetailsPresenter @Inject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val param: SeasonDetailsUiParam,
    @Assisted private val onBack: () -> Unit,
    @Assisted private val onEpisodeClick: (id: Long) -> Unit,
    private val seasonDetailsRepository: SeasonDetailsRepository,
) : ComponentContext by componentContext {

    private var seasonDetailsParam: SeasonDetailsParam
    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow<SeasonDetailsState>(Loading)
    val state: Value<SeasonDetailsState> = _state
        .asValue(initialValue = _state.value, lifecycle = lifecycle)

    init {
        seasonDetailsParam = SeasonDetailsParam(
            showId = param.showId,
            seasonId = param.seasonId,
            seasonNumber = param.seasonNumber,
        )
        coroutineScope.launch {
            fetchSeasonDetails()
            observeSeasonDetails()
        }
    }

    fun dispatch(action: SeasonDetailsAction) {
        coroutineScope.launch {
            when (action) {
                BackClicked -> onBack()
                is EpisodeClicked -> onEpisodeClick(action.id)
                is ReloadSeasonDetails -> fetchSeasonDetails()
                is UpdateEpisodeStatus -> {
                    // TODO:: Add implementation
                }
            }
        }
    }

    private suspend fun fetchSeasonDetails() {
        _state.value = Loading

        val seasonList = seasonDetailsRepository.fetchSeasonDetails(seasonDetailsParam)

        _state.value = SeasonDetailsLoaded(
            selectedSeason = seasonList.name,
            showTitle = seasonList.showTitle,
            seasonDetailsModel = seasonList.toSeasonDetails(),
        )
    }

    private suspend fun observeSeasonDetails() {
        seasonDetailsRepository.observeSeasonDetailsStream(seasonDetailsParam)
            .collect { seasonDetailsResult ->
                seasonDetailsResult.fold(
                    {
                        _state.update { state ->
                            (state as? SeasonDetailsLoaded)?.copy(
                                errorMessage = it.errorMessage,
                                isLoading = false,
                            ) ?: state
                        }
                    },
                    { result ->
                        _state.update { state ->
                            val detailsState = (state as? SeasonDetailsLoaded)
                            result?.let {
                                detailsState?.copy(
                                    showTitle = it.showTitle,
                                    seasonDetailsModel = result.toSeasonDetails(),
                                    isLoading = false,
                                )
                            } ?: state
                        }
                    },
                )
            }
    }
}
