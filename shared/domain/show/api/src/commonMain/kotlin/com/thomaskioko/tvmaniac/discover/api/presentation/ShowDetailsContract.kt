package com.thomaskioko.tvmaniac.discover.api.presentation

import com.thomaskioko.tvmaniac.discover.api.interactor.UpdateShowParams
import com.thomaskioko.tvmaniac.showcommon.api.TvShow
import com.thomaskioko.tvmaniac.genre.api.GenreUIModel
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisode
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel
import com.thomaskioko.tvmaniac.shared.core.store.Action
import com.thomaskioko.tvmaniac.shared.core.store.Effect
import com.thomaskioko.tvmaniac.shared.core.store.State

sealed class ShowDetailAction : Action {
    object LoadGenres : ShowDetailAction()
    object LoadSimilarShows : ShowDetailAction()

    data class LoadShowDetails(
        val showId: Long
    ) : ShowDetailAction()

    data class LoadSeasons(
        val showId: Long
    ) : ShowDetailAction()

    data class LoadEpisodes(
        val showId: Long
    ) : ShowDetailAction()

    data class UpdateFavorite(
        val params: UpdateShowParams
    ) : ShowDetailAction()

    data class BookmarkEpisode(
        val episodeNumber: Long
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
    val tvShow: com.thomaskioko.tvmaniac.showcommon.api.TvShow = com.thomaskioko.tvmaniac.showcommon.api.TvShow.EMPTY_SHOW,
    val similarShowList: List<com.thomaskioko.tvmaniac.showcommon.api.TvShow> = emptyList(),
    val tvSeasonUiModels: List<SeasonUiModel> = emptyList(),
    val genreUIList: List<GenreUIModel> = emptyList(),
    val lastAirEpList: List<LastAirEpisode> = emptyList(),
) : State {
    companion object {
        val Empty = ShowDetailViewState()
    }
}
