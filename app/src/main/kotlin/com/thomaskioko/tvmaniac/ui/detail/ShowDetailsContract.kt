package com.thomaskioko.tvmaniac.ui.detail

import com.thomaskioko.tvmaniac.interactor.EpisodeQuery
import com.thomaskioko.tvmaniac.presentation.model.Episode
import com.thomaskioko.tvmaniac.presentation.model.Season
import com.thomaskioko.tvmaniac.presentation.model.TvShow

sealed class ShowDetailAction {
    data class SeasonSelected(
        val query: EpisodeQuery
    ) : ShowDetailAction()
}


data class ShowDetailViewState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val tvShow: TvShow = TvShow.EMPTY_SHOW,
    val tvSeasons: List<Season> = emptyList(),
    val episodeList: List<Episode> = emptyList(),
    val episodesViewState: EpisodesViewState = EpisodesViewState()
) {
    companion object {
        val Empty = ShowDetailViewState()
    }
}

data class EpisodesViewState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val episodeList: List<Episode> = emptyList(),
) {
    companion object {
        val Empty = EpisodesViewState()
    }
}