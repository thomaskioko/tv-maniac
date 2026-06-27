package com.thomaskioko.tvmaniac.presenter.showdetails.cast

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.presenter.showdetails.model.CastModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class ShowDetailsCastState(
    val castsList: ImmutableList<CastModel> = persistentListOf(),
    val isRefreshing: Boolean = false,
    val message: UiMessage? = null,
)
