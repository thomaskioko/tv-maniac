package com.thomaskioko.tvmaniac.showdetails.nav.model

import kotlinx.serialization.Serializable

@Serializable
public data class ShowDetailsParam(
    val showId: Long,
    val forceRefresh: Boolean = false,
)
