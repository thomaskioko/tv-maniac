package com.thomaskioko.tvmaniac.following

import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect
import com.thomaskioko.tvmaniac.shows.api.model.TvShow

sealed class WatchlistState

data class FollowingState(
    val isLoading: Boolean,
    val list: List<TvShow>
) : WatchlistState() {
    companion object {
        val Empty = FollowingState(
            isLoading = true,
            list = emptyList()
        )
    }
}

sealed class FollowingAction : Action {
    object LoadFollowedShows : FollowingAction()
    data class Error(val message: String = "") : FollowingAction()
}

sealed class FollowingEffect : Effect {
    data class Error(val message: String = "") : FollowingEffect()
}
