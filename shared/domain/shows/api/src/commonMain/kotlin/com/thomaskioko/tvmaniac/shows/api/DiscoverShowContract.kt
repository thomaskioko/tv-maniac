package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.shows.api.DiscoverShowResult.DiscoverShowsData
import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.model.TvShow


data class DiscoverShowState(
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val featuredShows: DiscoverShowsData = DiscoverShowsData.Empty,
    val trendingShows: DiscoverShowsData = DiscoverShowsData.Empty,
    val recommendedShows: DiscoverShowsData = DiscoverShowsData.Empty,
    val popularShows: DiscoverShowsData = DiscoverShowsData.Empty,
    val anticipatedShows: DiscoverShowsData = DiscoverShowsData.Empty,
) {
    companion object {
        val Empty = DiscoverShowState()
    }
}


sealed class DiscoverShowAction : Action {
    object LoadTvShows : DiscoverShowAction()
    data class Error(val message: String = "") : DiscoverShowAction()
}

sealed class DiscoverShowEffect : Effect {
    data class Error(val message: String = "") : DiscoverShowEffect()
}

data class DiscoverShowResult(
    val isEmpty: Boolean = false,
    val featuredShows: DiscoverShowsData,
    val trendingShows: DiscoverShowsData,
    val recommendedShows: DiscoverShowsData,
    val popularShows: DiscoverShowsData,
    val anticipatedShows: DiscoverShowsData,
) {

    data class DiscoverShowsData(
        val isLoading: Boolean = false,
        val category: ShowCategory = ShowCategory.RECOMMENDED,
        val tvShows: List<TvShow> = emptyList(),
        val errorMessage: String? = null
    ) {
        companion object {
            val Empty = DiscoverShowsData()
        }
    }
}
