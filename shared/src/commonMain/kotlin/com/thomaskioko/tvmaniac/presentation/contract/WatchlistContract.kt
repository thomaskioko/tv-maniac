package com.thomaskioko.tvmaniac.presentation.contract

import com.thomaskioko.tvmaniac.presentation.model.ShowUiModel
import com.thomaskioko.tvmaniac.shared.core.store.Action
import com.thomaskioko.tvmaniac.shared.core.store.Effect
import com.thomaskioko.tvmaniac.shared.core.store.State

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
