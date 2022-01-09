package com.thomaskioko.tvmaniac.discover.api

import com.thomaskioko.tvmaniac.discover.api.model.ShowCategory
import com.thomaskioko.tvmaniac.discover.api.model.ShowUiModel
import com.thomaskioko.tvmaniac.shared.core.store.Action
import com.thomaskioko.tvmaniac.shared.core.store.Effect
import com.thomaskioko.tvmaniac.shared.core.store.State

data class DiscoverShowState(
    val isLoading: Boolean,
    val showData: DiscoverShowResult,
) : State {
    companion object {
        val Empty = DiscoverShowState(
            isLoading = true,
            showData = DiscoverShowResult.EMPTY,
        )
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
    val featuredShows: DiscoverShowsData,
    val trendingShows: DiscoverShowsData,
    val topRatedShows: DiscoverShowsData,
    val popularShows: DiscoverShowsData,
) {
    companion object {
        val EMPTY = DiscoverShowResult(
            featuredShows = DiscoverShowsData.EMPTY,
            trendingShows = DiscoverShowsData.EMPTY,
            topRatedShows = DiscoverShowsData.EMPTY,
            popularShows = DiscoverShowsData.EMPTY
        )
    }

    data class DiscoverShowsData(
        val isLoading: Boolean,
        val category: ShowCategory,
        val showUiModels: List<ShowUiModel>,
        val errorMessage: String? = null
    ) {

        companion object {
            val EMPTY = DiscoverShowsData(
                isLoading = true,
                category = ShowCategory.TOP_RATED,
                showUiModels = emptyList(),
                errorMessage = null
            )
        }
    }
}
