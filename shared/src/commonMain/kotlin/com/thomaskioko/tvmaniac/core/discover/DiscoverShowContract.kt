package com.thomaskioko.tvmaniac.core.discover

import com.thomaskioko.tvmaniac.core.Action
import com.thomaskioko.tvmaniac.core.Effect
import com.thomaskioko.tvmaniac.core.State
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.presentation.model.TvShow

data class DiscoverShowState(
    val featuredShows: DiscoverShowsData,
    val trendingShows: DiscoverShowsData,
    val topRatedShows: DiscoverShowsData,
    val popularShows: DiscoverShowsData,
) : State {
    companion object {
        val Empty = DiscoverShowState(
            featuredShows = DiscoverShowsData.EMPTY,
            trendingShows = DiscoverShowsData.EMPTY,
            topRatedShows = DiscoverShowsData.EMPTY,
            popularShows = DiscoverShowsData.EMPTY
        )
    }
}

sealed class DiscoverShowAction : Action {
    data class Error(val message: String = "") : DiscoverShowAction()
}

sealed class DiscoverShowEffect : Effect {
    data class Error(val message: String = "") : DiscoverShowEffect()
}

data class DiscoverShowsData(
    val isLoading: Boolean,
    val category: ShowCategory,
    val shows: List<TvShow>,
    val errorMessage: String? = null
) {
    companion object {
        val EMPTY = DiscoverShowsData(
            isLoading = true,
            category = ShowCategory.TOP_RATED,
            shows = emptyList(),
            errorMessage = null
        )
    }
}
