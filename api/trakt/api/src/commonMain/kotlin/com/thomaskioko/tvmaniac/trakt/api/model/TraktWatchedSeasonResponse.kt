package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktWatchedSeasonResponse(
    @SerialName("number") val number: Long,
    @SerialName("aired") val aired: Long,
    @SerialName("completed") val completed: Long,
)
