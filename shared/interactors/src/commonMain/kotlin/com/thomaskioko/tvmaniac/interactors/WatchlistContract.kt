package com.thomaskioko.tvmaniac.interactors

import com.thomaskioko.tvmaniac.shared.core.store.Action
import com.thomaskioko.tvmaniac.shared.core.store.Effect
import com.thomaskioko.tvmaniac.shared.core.store.State
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow

data class WatchlistState(
    val isLoading: Boolean,
    val list: List<TvShow>
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
