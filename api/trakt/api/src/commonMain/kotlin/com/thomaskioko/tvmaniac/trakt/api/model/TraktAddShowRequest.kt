package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
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

@OptIn(ExperimentalSerializationApi::class)
@Serializable
public data class TraktShowIds(
    @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("trakt") val traktId: Long? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER) @SerialName("tmdb") val tmdbId: Long? = null,
)
