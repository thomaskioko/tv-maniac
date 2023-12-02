package com.thomaskioko.tvmaniac.presentation.moreshows

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MoreShowsState(
    val isLoading: Boolean = false,
    val categoryTitle: String? = null,
    val list: ImmutableList<TvShow> = persistentListOf(),
    val errorMessage: String? = null,
)
