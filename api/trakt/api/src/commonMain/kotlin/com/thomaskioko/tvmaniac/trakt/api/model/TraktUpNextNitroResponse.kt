package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Provisional shape based on the official Trakt Android app's
// /sync/progress/up_next_nitro caller. Phase 0 of the continue-watching PRD
// captures a real response to lock the model.
@Serializable
public data class TraktUpNextNitroResponse(
    @SerialName("show") val show: TraktShowResponse,
    @SerialName("progress") val progress: TraktWatchedProgressResponse,
)
