package com.thomaskioko.tvmaniac.following

import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect
import com.thomaskioko.tvmaniac.shared.core.ui.State
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
