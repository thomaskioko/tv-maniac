package com.thomaskioko.tvmaniac.presentation.contract

import com.thomaskioko.tvmaniac.core.Action
import com.thomaskioko.tvmaniac.core.Effect
import com.thomaskioko.tvmaniac.core.State
import com.thomaskioko.tvmaniac.presentation.model.TvShow


data class ShowsGridState(
    val isLoading: Boolean,
    val title : String,
    val list: List<TvShow>
) : State {
    companion object {
        val Empty = ShowsGridState(
            isLoading = true,
            title = "",
            list = emptyList()
        )
    }
}

sealed class ShowsGridAction : Action {
    object LoadTvShows  : ShowsGridAction()
    data class Error(val message: String = "") : ShowsGridAction()
}

sealed class ShowsGridEffect : Effect {
    data class Error(val message: String = "") : ShowsGridEffect()
}