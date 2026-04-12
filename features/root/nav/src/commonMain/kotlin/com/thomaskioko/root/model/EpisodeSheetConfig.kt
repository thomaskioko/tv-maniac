package com.thomaskioko.root.model

import kotlinx.serialization.Serializable

@Serializable
public data class EpisodeSheetConfig(
    val episodeId: Long,
    val source: ScreenSource,
)
