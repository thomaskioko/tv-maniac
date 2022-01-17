package com.thomaskioko.showdetails

import com.thomaskioko.tvmaniac.discover.api.interactor.UpdateShowParams
import com.thomaskioko.tvmaniac.discover.api.model.ShowUiModel
import com.thomaskioko.tvmaniac.episodes.api.EpisodeQuery
import com.thomaskioko.tvmaniac.episodes.api.EpisodeUiModel
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel
import com.thomaskioko.tvmaniac.shared.core.store.Action
import com.thomaskioko.tvmaniac.shared.core.store.Effect
import com.thomaskioko.tvmaniac.shared.core.store.State

sealed class ShowDetailAction : Action {
    object LoadShowDetails : ShowDetailAction()
    object LoadSeasons : ShowDetailAction()
    object LoadGenres : ShowDetailAction()

    data class SeasonSelected(
        val query: EpisodeQuery
    ) : ShowDetailAction()

    data class LoadEpisodes(
        val query: EpisodeQuery
    ) : ShowDetailAction()

    data class UpdateWatchlist(
        val params: UpdateShowParams
    ) : ShowDetailAction()

    data class Error(val message: String = "") : ShowDetailAction()
}

sealed class ShowDetailEffect : Effect {
    data class ShowDetailsError(val errorMessage: String = "") : ShowDetailEffect()

    data class WatchlistError(
        var errorMessage: String
    ) : ShowDetailEffect()
}

data class ShowDetailViewState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val showUiModel: ShowUiModel = ShowUiModel.EMPTY_SHOW,
    val tvSeasonUiModels: List<SeasonUiModel> = emptyList(),
    val genreUIList: List<com.thomaskioko.tvmaniac.genre.api.GenreUIModel> = emptyList(),
    val episodeList: List<EpisodeUiModel> = emptyList(),
) : State {
    companion object {
        val Empty = ShowDetailViewState()
    }
}
