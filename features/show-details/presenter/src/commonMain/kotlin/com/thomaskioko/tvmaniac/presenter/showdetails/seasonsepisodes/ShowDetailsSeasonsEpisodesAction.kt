package com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes

import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowSeasonDetailsParam

public sealed interface ShowDetailsSeasonsEpisodesAction

public data class ShowDetailsSeasonClicked(val params: ShowSeasonDetailsParam) : ShowDetailsSeasonsEpisodesAction

public data class ShowDetailsMarkEpisodeWatched(
    val showId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
) : ShowDetailsSeasonsEpisodesAction

public data class ShowDetailsMarkEpisodeUnwatched(
    val showId: Long,
    val episodeId: Long,
) : ShowDetailsSeasonsEpisodesAction
