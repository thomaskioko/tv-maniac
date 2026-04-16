package com.thomaskioko.tvmaniac.espisodedetails.nav.model

import com.thomaskioko.tvmaniac.navigation.SheetConfig
import kotlinx.serialization.Serializable

@Serializable
public data class EpisodeSheetConfig(
    val episodeId: Long,
    val source: ScreenSource,
) : SheetConfig
