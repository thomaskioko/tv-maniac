package com.thomaskioko.tvmaniac.trakt.service.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TraktAddShowToListResponse(
    @SerialName("added") val added: TraktAddedShowsResponse,
    @SerialName("existing") val existing: TraktExistingShowsResponse,
    @SerialName("not_found") val notFound: TraktNotFoundShowsResponse,
    @SerialName("list") val list: TraktListResponse
)

@Serializable
data class TraktAddedShowsResponse(
    @SerialName("shows") val shows: Int
)

@Serializable
data class TraktExistingShowsResponse(
    @SerialName("shows") val shows: Int
)

@Serializable
data class TraktNotFoundShowsResponse(
    @SerialName("shows") val shows: List<TraktNotFoundShows>
)

@Serializable
data class TraktListResponse(
    @SerialName("item_count") val itemCount: Int,
    @SerialName("updated_at") val updateAdd: String
)

@Serializable
data class TraktNotFoundShows(
    @SerialName("trakt") val trakt: Int,
    @SerialName("tmdb") val tmdb: Int,
)