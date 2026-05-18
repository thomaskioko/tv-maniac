package com.thomaskioko.tvmaniac.domain.upnext.model

import com.thomaskioko.tvmaniac.domain.upnext.model.UpNextSortOption
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow

public data class UpNextResult(
    val sortOption: UpNextSortOption,
    val episodes: List<NextEpisodeWithShow>,
)
