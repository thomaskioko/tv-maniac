package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class CreditsResponse(
    @SerialName("cast") var cast: ArrayList<CastResponse> = arrayListOf(),
)

@Serializable
public data class CastResponse(
    @SerialName("id") var id: Int,
    @SerialName("name") var name: String,
    @SerialName("profile_path") var profilePath: String? = null,
    @SerialName("character") var character: String,
    @SerialName("popularity") var popularity: Double,
)
