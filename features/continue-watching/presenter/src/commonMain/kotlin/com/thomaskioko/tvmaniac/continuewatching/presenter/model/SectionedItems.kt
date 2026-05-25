package com.thomaskioko.tvmaniac.continuewatching.presenter.model

import kotlinx.collections.immutable.ImmutableList

public data class SectionedItems(
    val watchNext: ImmutableList<ContinueWatchingItem>,
    val stale: ImmutableList<ContinueWatchingItem>,
)
