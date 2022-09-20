package com.thomaskioko.tvmaniac.show_grid

import com.kuuurt.paging.multiplatform.PagingData
import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect
import com.thomaskioko.tvmaniac.shows.api.model.TvShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

sealed class ShowsGridState

data class ShowsLoaded(
    val isLoading: Boolean,
    val title: String,
    val list: Flow<PagingData<TvShow>>
) : ShowsGridState() {
    companion object {
        val Empty: ShowsLoaded = ShowsLoaded(
            isLoading = true,
            title = "",
            list = flowOf()
        )
    }
}

sealed class ShowsGridAction : Action {
    object LoadTvShows : ShowsGridAction()
    data class Error(val message: String = "") : ShowsGridAction()
}

sealed class ShowsGridEffect : Effect {
    data class Error(val message: String? = "") : ShowsGridEffect()
}
