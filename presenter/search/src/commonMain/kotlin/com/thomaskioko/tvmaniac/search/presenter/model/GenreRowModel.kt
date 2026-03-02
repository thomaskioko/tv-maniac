package com.thomaskioko.tvmaniac.search.presenter.model

import kotlinx.collections.immutable.ImmutableList

public data class GenreRowModel(
    val slug: String,
    val name: String,
    val subtitle: String,
    val shows: ImmutableList<ShowItem>,
)
