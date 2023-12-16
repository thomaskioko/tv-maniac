package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreditsResponse(
    @SerialName("cast") var cast: ArrayList<CastResponse> = arrayListOf(),
)

@Serializable
data class CastResponse(
    @SerialName("id") var id: Int,
    @SerialName("name") var name: String? = null,
    @SerialName("profile_path") var profilePath: String? = null,
    @SerialName("character") var character: String,
)
