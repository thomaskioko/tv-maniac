package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktCreateListRequest(
    @SerialName("name") val name: String = "Following",
    @SerialName("privacy") val privacy: String = "private",
    @SerialName("sort_by") val sortBy: String = "added",
    @SerialName("sort_how") val sortHow: String = "asc",
    @SerialName("description") val description: String = "Your list of followed shows on TvManiac.",
    @SerialName("display_numbers") val displayNumbers: Boolean = false,
    @SerialName("allow_comments") val allowComments: Boolean = false,
)
