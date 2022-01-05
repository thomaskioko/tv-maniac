package com.thomaskioko.tvmaniac.presentation.contract

import com.thomaskioko.tvmaniac.core.Action
import com.thomaskioko.tvmaniac.core.Effect
import com.thomaskioko.tvmaniac.core.State
import com.thomaskioko.tvmaniac.presentation.model.ShowUiModel

data class WatchlistState(
    val isLoading: Boolean,
    val list: List<ShowUiModel>
) : State {
    companion object {
        val Empty = WatchlistState(
            isLoading = true,
            list = emptyList()
        )
    }
}

sealed class WatchlistAction : Action {
    object LoadWatchlist : WatchlistAction()
    data class Error(val message: String = "") : WatchlistAction()
}

sealed class WatchlistEffect : Effect {
    data class Error(val message: String = "") : WatchlistEffect()
}
