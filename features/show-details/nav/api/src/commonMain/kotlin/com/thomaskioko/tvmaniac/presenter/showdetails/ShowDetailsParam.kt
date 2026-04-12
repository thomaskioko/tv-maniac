package com.thomaskioko.tvmaniac.presenter.showdetails

import kotlinx.serialization.Serializable

@Serializable
public data class ShowDetailsParam(
    val id: Long,
    val forceRefresh: Boolean = false,
)
