package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktAddShowRequest(
    @SerialName("shows") val shows: List<TraktShow>,
)

@Serializable
public data class TraktShow(
    @SerialName("ids") val ids: TraktShowIds,
)

@Serializable
public data class TraktShowIds(
    @SerialName("trakt") val traktId: Long? = null,
    @SerialName("tmdb") val tmdbId: Long? = null,
)
