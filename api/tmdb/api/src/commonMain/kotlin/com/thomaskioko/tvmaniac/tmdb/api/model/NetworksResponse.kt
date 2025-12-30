package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class NetworksResponse(
    @SerialName("id") var id: Int,
    @SerialName("logo_path") var logoPath: String,
    @SerialName("name") var name: String,
)
