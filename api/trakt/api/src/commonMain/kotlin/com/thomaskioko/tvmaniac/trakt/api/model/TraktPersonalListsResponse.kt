package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktPersonalListsResponse(
    @SerialName("allow_comments") val allowComments: Boolean,
    @SerialName("comment_count") val commentCount: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("description") val description: String,
    @SerialName("display_numbers") val display_numbers: Boolean,
    @SerialName("ids") val ids: ListIds,
    @SerialName("item_count") val item_count: Int,
    @SerialName("likes") val likes: Int,
    @SerialName("name") val name: String,
    @SerialName("privacy") val privacy: String,
    @SerialName("sort_by") val sort_by: String,
    @SerialName("sort_how") val sort_how: String,
    @SerialName("updated_at") val updated_at: String,
)
