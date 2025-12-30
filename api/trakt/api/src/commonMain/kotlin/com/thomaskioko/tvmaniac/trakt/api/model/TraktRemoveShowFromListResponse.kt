package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktAddRemoveShowFromListResponse(
    @SerialName("deleted") val deleted: TraktDeletedShowsResponse,
    @SerialName("not_found") val notFound: TraktNotFoundShowsResponse,
    @SerialName("list") val list: TraktListResponse,
)

@Serializable
public data class TraktDeletedShowsResponse(
    @SerialName("shows") val shows: Int,
)
