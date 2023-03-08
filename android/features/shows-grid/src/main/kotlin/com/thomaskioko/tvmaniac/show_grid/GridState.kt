package com.thomaskioko.tvmaniac.show_grid

import com.thomaskioko.tvmaniac.show_grid.model.TvShow


sealed interface GridState

object LoadingContent: GridState

data class ShowsLoaded(
    val list: List<TvShow>
) : GridState

data class LoadingContentError(val errorMessage: String) : GridState
