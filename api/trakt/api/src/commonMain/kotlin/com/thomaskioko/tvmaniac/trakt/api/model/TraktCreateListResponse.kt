package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktCreateListResponse(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("privacy") val privacy: String,
    @SerialName("ids") val ids: ListIds,
)

@Serializable
public data class ListIds(
    @SerialName("trakt") val trakt: Int,
    @SerialName("slug") val slug: String,
)
