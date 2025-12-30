package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktAddShowToListResponse(
    @SerialName("added") val added: TraktAddedShowsResponse,
    @SerialName("existing") val existing: TraktExistingShowsResponse,
    @SerialName("not_found") val notFound: TraktNotFoundShowsResponse,
    @SerialName("list") val list: TraktListResponse,
)

@Serializable
public data class TraktAddedShowsResponse(
    @SerialName("shows") val shows: Int,
)

@Serializable
public data class TraktExistingShowsResponse(
    @SerialName("shows") val shows: Int,
)

@Serializable
public data class TraktNotFoundShowsResponse(
    @SerialName("shows") val shows: List<TraktNotFoundShows>,
)

@Serializable
public data class TraktListResponse(
    @SerialName("item_count") val itemCount: Int,
    @SerialName("updated_at") val updateAdd: String,
)

@Serializable
public data class TraktNotFoundShows(
    @SerialName("trakt") val trakt: Int,
    @SerialName("tmdb") val tmdb: Int,
)
