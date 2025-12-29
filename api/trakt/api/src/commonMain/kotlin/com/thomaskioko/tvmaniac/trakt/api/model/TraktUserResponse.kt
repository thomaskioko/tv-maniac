package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktUserResponse(
    @SerialName("username") val userName: String,
    @SerialName("name") val name: String,
    @SerialName("images") val images: ProfileImages,
    @SerialName("ids") val ids: Ids,
)

@Serializable
public data class Ids(
    @SerialName("slug") val slug: String,
)

@Serializable
public data class ProfileImages(
    @SerialName("avatar") val avatar: Avatar,
) {
    @Serializable
    public data class Avatar(
        @SerialName("full") val full: String,
    )
}
