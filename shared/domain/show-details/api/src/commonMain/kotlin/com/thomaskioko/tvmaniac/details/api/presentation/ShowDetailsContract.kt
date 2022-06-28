package com.thomaskioko.tvmaniac.details.api.presentation

import com.thomaskioko.tvmaniac.details.api.interactor.UpdateShowParams
import com.thomaskioko.tvmaniac.genre.api.GenreUIModel
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisode
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel
import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.model.Trailer
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow

sealed class ShowDetailAction : Action {
    data class LoadShowDetails(
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
    val tvShow: TvShow = TvShow.EMPTY_SHOW,
    val similarShowList: List<TvShow> = emptyList(),
    val tvSeasonUiModels: List<SeasonUiModel> = emptyList(),
    val genreUIList: List<GenreUIModel> = emptyList(),
    val lastAirEpList: List<LastAirEpisode> = emptyList(),
    val trailersList: List<Trailer> = emptyList(),
)  {
    companion object {
        val Empty = ShowDetailViewState()
    }
}
