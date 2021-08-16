package com.thomaskioko.tvmaniac.core.discover

import com.thomaskioko.tvmaniac.core.Action
import com.thomaskioko.tvmaniac.core.Effect
import com.thomaskioko.tvmaniac.core.State
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.presentation.model.TvShow


data class DiscoverShowState(
    val isLoading: Boolean,
    val dataMap: LinkedHashMap<ShowCategory, List<TvShow>>
) : State {
    companion object {
        val Empty = DiscoverShowState(
            isLoading = true,
            dataMap = linkedMapOf()
        )
    }
}

sealed class DiscoverShowAction : Action {
    data class LoadTvShows(
        val tvShowType: List<ShowCategory>
    ) : DiscoverShowAction()

    data class Error(val message: String = "") : DiscoverShowAction()
}

sealed class DiscoverShowEffect : Effect {
    data class Error(val message: String = "") : DiscoverShowEffect()
}