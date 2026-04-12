package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.nav.model.ScreenSource
import kotlinx.serialization.Serializable

@Serializable
public data class EpisodeSheetConfig(
    val episodeId: Long,
    val source: ScreenSource,
)
