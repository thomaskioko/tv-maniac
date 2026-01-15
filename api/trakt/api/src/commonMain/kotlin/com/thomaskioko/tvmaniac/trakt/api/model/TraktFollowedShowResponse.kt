package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktFollowedShowResponse(
    @SerialName("rank") var rank: Int,
    @SerialName("id") var id: Int,
    @SerialName("listed_at") var listedAt: String,
    @SerialName("notes") var notes: String? = null,
    @SerialName("type") var type: String,
    @SerialName("show") var show: ShowResponse,
)

@Serializable
public data class ShowResponse(
    @SerialName("title") var title: String,
    @SerialName("year") var year: Int? = null,
    @SerialName("ids") var ids: IdsResponse,
)

@Serializable
public data class IdsResponse(
    @SerialName("slug") var slug: String,
    @SerialName("trakt") var trakt: Long,
    @SerialName("tmdb") var tmdb: Long,
    @SerialName("tvdb") var tvdb: Long? = null,
    @SerialName("imdb") var imdb: String? = null,
)
