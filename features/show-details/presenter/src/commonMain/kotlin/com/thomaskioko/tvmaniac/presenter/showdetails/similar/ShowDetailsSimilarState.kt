package com.thomaskioko.tvmaniac.presenter.showdetails.similar

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class ShowDetailsSimilarState(
    val similarShows: ImmutableList<ShowModel> = persistentListOf(),
    val isRefreshing: Boolean = false,
    val message: UiMessage? = null,
)
