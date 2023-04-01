package com.thomaskioko.tvmaniac.trakt.service.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TraktUserResponse(
    @SerialName("username") val userName: String,
    @SerialName("name") val name: String,
    @SerialName("images") val images: ProfileImages,
    @SerialName("ids") val ids: Ids,
)

@Serializable
data class Ids(
    @SerialName("slug") val slug: String
)

@Serializable
data class ProfileImages(
    @SerialName("avatar") val avatar: Avatar
) {
    @Serializable
    data class Avatar(
        @SerialName("full") val full: String
    )
}