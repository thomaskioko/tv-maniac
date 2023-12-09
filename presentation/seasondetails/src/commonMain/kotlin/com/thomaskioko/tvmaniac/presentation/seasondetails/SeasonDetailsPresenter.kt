package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.util.decompose.asValue
import com.thomaskioko.tvmaniac.util.decompose.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias SeasonDetailsPresenterFactory = (
    ComponentContext,
    id: Long,
    title: String?,
    onBack: () -> Unit,
    onNavigateToEpisodeDetails: (id: Long) -> Unit,
) -> SeasonDetailsPresenter

class SeasonDetailsPresenter @Inject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val traktId: Long,
    @Assisted private val title: String?,
    @Assisted private val onBack: () -> Unit,
    @Assisted private val onEpisodeClick: (id: Long) -> Unit,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val episodeImageRepository: EpisodeImageRepository,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow<SeasonDetailsState>(Loading)
    val state: Value<SeasonDetailsState> = _state
        .asValue(initialValue = _state.value, lifecycle = lifecycle)

    init {
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

        val seasonList = seasonDetailsRepository.fetchSeasonDetails(traktId)

        _state.value = SeasonDetailsLoaded(
            selectedSeason = title,
            showTitle = seasonList.getTitle(),
            seasonDetailsList = seasonList.toSeasonWithEpisodes(),
        )
    }

    private suspend fun observeSeasonDetails() {
        combine(
            seasonDetailsRepository.observeSeasonDetailsStream(traktId),
            episodeImageRepository.updateEpisodeImage(traktId),
        ) { seasonDetailsResult, _ ->
            seasonDetailsResult.fold(
                {
                    _state.update { state ->
                        (state as? SeasonDetailsLoaded)?.copy(
                            errorMessage = it.errorMessage,
                        ) ?: state
                    }
                },
                {
                    _state.update { state ->
                        (state as? SeasonDetailsLoaded)?.copy(
                            showTitle = it.getTitle(),
                            seasonDetailsList = it.toSeasonWithEpisodes(),
                        ) ?: state
                    }
                },
            )
        }
            .collect()
    }
}
