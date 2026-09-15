package com.thomaskioko.tvmaniac.simkl.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SimklSearchShowResponse(
    @SerialName("title") val title: String,
    @SerialName("year") val year: Int? = null,
    @SerialName("poster") val poster: String? = null,
    @SerialName("ids") val ids: SimklSearchIds,
)

@Serializable
public data class SimklSearchIds(
    @SerialName("simkl_id") val simklId: Long? = null,
    @SerialName("slug") val slug: String? = null,
    @SerialName("tmdb") val tmdb: String? = null,
    @SerialName("imdb") val imdb: String? = null,
    @SerialName("tvdb") val tvdb: String? = null,
)
