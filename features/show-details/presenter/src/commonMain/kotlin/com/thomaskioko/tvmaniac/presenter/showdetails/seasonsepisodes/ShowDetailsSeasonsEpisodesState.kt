package com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ContinueTrackingEpisodeModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.SeasonModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

public data class ShowDetailsSeasonsEpisodesState(
    val seasonsList: ImmutableList<SeasonModel> = persistentListOf(),
    val numberOfSeasons: Int = 0,
    val watchedEpisodesCount: Int = 0,
    val totalEpisodesCount: Int = 0,
    val watchProgress: Float = 0f,
    val selectedSeasonIndex: Int = -1,
    val continueTrackingEpisodes: ImmutableList<ContinueTrackingEpisodeModel> = persistentListOf(),
    val continueTrackingScrollIndex: Int = 0,
    val updatingEpisodeIds: ImmutableSet<Long> = persistentSetOf(),
    val isRefreshing: Boolean = false,
    val isUpdating: Boolean = false,
    val message: UiMessage? = null,
)
