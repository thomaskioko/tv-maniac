package com.thomaskioko.tvmaniac.showsgrid

import com.thomaskioko.tvmaniac.showsgrid.model.TvShow

sealed interface GridState

object LoadingContent : GridState

data class ShowsLoaded(
    val list: List<TvShow>,
) : GridState

data class LoadingContentError(val errorMessage: String? = null) : GridState
