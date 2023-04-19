package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TraktAddShowRequest(
    @SerialName("shows") val shows: List<TraktShow>,
)

@Serializable
data class TraktShow(
    @SerialName("ids") val ids: TraktShowIds,
)

@Serializable
data class TraktShowIds(
    @SerialName("trakt") val traktId: Int,
)
