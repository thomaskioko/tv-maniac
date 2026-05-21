package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktHiddenItemResponse(
    @SerialName("hidden_at") val hiddenAt: String,
    @SerialName("type") val type: String,
    @SerialName("show") val show: TraktShowResponse? = null,
)
