package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowDetailsModel

data class ShowDetailsContent(
    val recommendedShowsRefreshing: Boolean = false,
    val showDetailsRefreshing: Boolean = false,
    val similarShowsRefreshing: Boolean = false,
    val watchProvidersRefreshing: Boolean = false,
    val showListSheet: Boolean = false,
    val showDetails: ShowDetailsModel = ShowDetailsModel.Empty,
    val selectedSeasonIndex: Int = 0,
    val message: UiMessage? = null,
) {
    val isRefreshing: Boolean
        get() = recommendedShowsRefreshing || showDetailsRefreshing || similarShowsRefreshing || watchProvidersRefreshing

    companion object {
        val Empty = ShowDetailsContent()
    }
}
