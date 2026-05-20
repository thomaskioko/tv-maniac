package com.thomaskioko.tvmaniac.domain.continuewatching.model

import com.thomaskioko.tvmaniac.upnext.api.model.UpNextEpisode

public data class UpNextResult(
    val sortOption: UpNextSortOption,
    val episodes: List<UpNextEpisode>,
)
