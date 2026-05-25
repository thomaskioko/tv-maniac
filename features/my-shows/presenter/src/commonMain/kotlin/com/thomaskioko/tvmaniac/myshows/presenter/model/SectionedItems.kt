package com.thomaskioko.tvmaniac.myshows.presenter.model

import kotlinx.collections.immutable.ImmutableList

public data class SectionedItems(
    val watchNext: ImmutableList<MyShowsItem>,
    val stale: ImmutableList<MyShowsItem>,
)
