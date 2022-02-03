package com.thomaskioko.tvmaniac.discover.api

import com.thomaskioko.tvmaniac.discover.api.interactor.ObserveShowInteractor
import com.thomaskioko.tvmaniac.discover.api.interactor.UpdateFollowingInteractor
import com.thomaskioko.tvmaniac.discover.api.presentation.ShowDetailAction
import com.thomaskioko.tvmaniac.genre.api.GenreUIModel
import com.thomaskioko.tvmaniac.genre.api.GetGenresInteractor
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisode
import com.thomaskioko.tvmaniac.lastairepisodes.api.ObserveAirEpisodesInteractor
import com.thomaskioko.tvmaniac.seasons.api.interactor.ObserveSeasonsInteractor
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel
import com.thomaskioko.tvmaniac.shared.core.BaseViewModel
import com.thomaskioko.tvmaniac.shared.core.store.Action
import com.thomaskioko.tvmaniac.shared.core.store.ViewState
import com.thomaskioko.tvmaniac.similar.api.ObserveSimilarShowsInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ShowDetailsViewModel : BaseViewModel(), KoinComponent {

    private val observeShow: ObserveShowInteractor by inject()
    private val observeSeasonsInteractor: ObserveSeasonsInteractor by inject()
    private val genresInteractor: GetGenresInteractor by inject()
    private val observeAirEpisodesInteractor: ObserveAirEpisodesInteractor by inject()
    private val updateFollowingInteractor: UpdateFollowingInteractor by inject()
    private val observeSimilarShows: ObserveSimilarShowsInteractor by inject()

    private val _uiState = MutableStateFlow(ShowDetailUiViewState.Empty)

    override val state: StateFlow<ShowDetailUiViewState>
        get() = _uiState

    override fun attach() {
        dispatch(ShowDetailAction.LoadGenres)
    }

    override fun dispatch(action: Action) {
        when (action) {
            is ShowDetailAction.BookmarkEpisode -> {
                // TODO:: Update episode watchlist
            }
            is ShowDetailAction.Error -> {
            }

            is ShowDetailAction.LoadShowDetails -> fetchShowDetails(action)
            is ShowDetailAction.UpdateFavorite -> updateFollowingShow(action)
            ShowDetailAction.LoadSimilarShows -> {
            }
        }
    }

    private fun fetchShowDetails(action: ShowDetailAction.LoadShowDetails) {
        vmScope.launch {
            combine(
                observeShow.invoke(action.showId),
                observeSimilarShows.invoke(action.showId),
                observeSeasonsInteractor.invoke(action.showId),
                observeAirEpisodesInteractor.invoke(action.showId),
                genresInteractor.invoke(Unit)
            ) { show, similarShows, seasons, airList, genres ->

                ShowDetailUiViewState(
                    tvShow = show,
                    seasonList = seasons,
                    genreList = genres.filter { it.id in show.genreIds },
                    episodeList = airList,
                    similarShowList = similarShows
                )
            }
                .catch { dispatch(ShowDetailAction.Error(it.message ?: "Something went wrong")) }
                .collect {
                    _uiState.emit(it)
                }
        }
    }

    private fun updateFollowingShow(action: ShowDetailAction.UpdateFavorite) {

        updateFollowingInteractor.invoke(action.params)
            .onCompletion {
                dispatch(ShowDetailAction.LoadShowDetails(action.params.showId))
            }
            .launchIn(vmScope)
    }
}

data class ShowDetailUiViewState(
    val isLoading: Boolean = true,
    val errorMessage: String = "",
    val tvShow: com.thomaskioko.tvmaniac.showcommon.api.TvShow = com.thomaskioko.tvmaniac.showcommon.api.TvShow.EMPTY_SHOW,
    val similarShowList: List<com.thomaskioko.tvmaniac.showcommon.api.TvShow> = emptyList(),
    val seasonList: List<SeasonUiModel> = emptyList(),
    val genreList: List<GenreUIModel> = emptyList(),
    val episodeList: List<LastAirEpisode> = emptyList(),
) : ViewState() {
    companion object {
        val Empty = ShowDetailUiViewState()
    }
}
