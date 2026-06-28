package com.thomaskioko.tvmaniac.presenter.showdetails.trailers

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.presenter.showdetails.model.TrailerModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class ShowDetailsTrailersState(
    val trailersList: ImmutableList<TrailerModel> = persistentListOf(),
    val hasWebViewInstalled: Boolean = false,
    val isRefreshing: Boolean = false,
    val message: UiMessage? = null,
)
