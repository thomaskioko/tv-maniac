package com.thomaskioko.tvmaniac.following

import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow

sealed class WatchlistState

data class WatchlistLoaded(
    val isLoading: Boolean,
    val list: List<TvShow>
) : WatchlistState() {
    companion object {
        val Empty = WatchlistLoaded(
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
