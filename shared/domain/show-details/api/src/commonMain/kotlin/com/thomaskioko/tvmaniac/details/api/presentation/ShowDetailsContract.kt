package com.thomaskioko.tvmaniac.details.api.presentation

import com.thomaskioko.tvmaniac.details.api.interactor.UpdateShowParams
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisode
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel
import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.model.Trailer
import com.thomaskioko.tvmaniac.shows.api.model.TvShow

sealed class ShowDetailAction : Action {
    data class LoadShowDetails(
        val traktId: Int
    ) : ShowDetailAction()

    data class UpdateFollowing(
        val params: UpdateShowParams
    ) : ShowDetailAction()

    data class BookmarkEpisode(
        val episodeNumber: Int
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
    val isFollowed: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isFollowUpdating: Boolean = false,
    val selectedVideoKey: String? = null,
    val errorMessage: String? = null,
    val tvShow: TvShow = TvShow.EMPTY_SHOW,
    val similarShowList: List<TvShow> = emptyList(),
    val tvSeasonUiModels: List<SeasonUiModel> = emptyList(),
    val lastAirEpList: List<LastAirEpisode> = emptyList(),
    val trailersList: List<Trailer> = emptyList(),
)  {
    companion object {
        val Empty = ShowDetailViewState()
    }
}
