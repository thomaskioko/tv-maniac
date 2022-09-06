package com.thomaskioko.tvmaniac.discover.api

import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect
import com.thomaskioko.tvmaniac.showcommon.api.model.ShowCategory
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow


sealed class DiscoverShowState

object Loading : DiscoverShowState()
data class ErrorState(val message: String = "")  : DiscoverShowState()

data class DataLoaded(
    val showData: DiscoverShowResult,
) : DiscoverShowState()


sealed class DiscoverShowAction : Action {
    object LoadTvShows : DiscoverShowAction()
    data class Error(val message: String = "") : DiscoverShowAction()
    data class DataLoaded(
        val showData: DiscoverShowResult,
    ) : DiscoverShowAction()
}

sealed class DiscoverShowEffect : Effect {
    data class Error(val message: String = "") : DiscoverShowEffect()
}

data class DiscoverShowResult(
    val featuredShows: DiscoverShowsData,
    val trendingShows: DiscoverShowsData,
    val recommendedShows: DiscoverShowsData,
    val popularShows: DiscoverShowsData,
    val anticipatedShows: DiscoverShowsData,
) {

    data class DiscoverShowsData(
        val isLoading: Boolean = false,
        val category: ShowCategory,
        val tvShows: List<TvShow>,
        val errorMessage: String? = null
    )
}
