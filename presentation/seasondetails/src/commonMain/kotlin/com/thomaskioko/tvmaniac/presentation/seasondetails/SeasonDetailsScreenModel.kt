package com.thomaskioko.tvmaniac.presentation.seasondetails

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class SeasonDetailsScreenModel @Inject constructor(
    @Assisted private val traktId: Long,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val episodeImageRepository: EpisodeImageRepository,
) : ScreenModel {

    private val _state = MutableStateFlow<SeasonDetailsState>(Loading)
    val state: StateFlow<SeasonDetailsState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            fetchSeasonDetails()
            observeSeasonDetails()
        }
    }

    fun dispatch(action: SeasonDetailsAction) {
        screenModelScope.launch {
            when (action) {
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
