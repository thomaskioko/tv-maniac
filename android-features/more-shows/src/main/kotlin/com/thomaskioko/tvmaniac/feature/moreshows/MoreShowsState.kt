package com.thomaskioko.tvmaniac.feature.moreshows

import com.thomaskioko.tvmaniac.feature.moreshows.model.TvShow
import kotlinx.collections.immutable.ImmutableList

sealed interface GridState

data object LoadingContent : GridState

data class ShowsLoaded(
    val list: ImmutableList<TvShow>,
) : GridState

data class LoadingContentError(val errorMessage: String? = null) : GridState
