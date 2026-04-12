package com.thomaskioko.tvmaniac.watchlist.presenter.model

import kotlinx.collections.immutable.ImmutableList

public data class SectionedItems(
    val watchNext: ImmutableList<WatchlistItem>,
    val stale: ImmutableList<WatchlistItem>,
)
