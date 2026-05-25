package com.thomaskioko.tvmaniac.myshows.presenter.model

import kotlinx.collections.immutable.ImmutableList

public data class SectionedEpisodes(
    val watchNext: ImmutableList<UpNextEpisodeItem>,
    val stale: ImmutableList<UpNextEpisodeItem>,
)
