package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("status_code") val statusCode: String,
    @SerialName("status_message") val message: String,
)
