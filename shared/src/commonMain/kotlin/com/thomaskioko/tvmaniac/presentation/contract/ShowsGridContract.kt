package com.thomaskioko.tvmaniac.presentation.contract

import com.kuuurt.paging.multiplatform.PagingData
import com.thomaskioko.tvmaniac.core.Action
import com.thomaskioko.tvmaniac.core.Effect
import com.thomaskioko.tvmaniac.core.State
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.CommonFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class ShowsGridState(
    val isLoading: Boolean,
    val title: String,
    val list: Flow<PagingData<TvShow>>
) : State {
    companion object {
        val Empty: ShowsGridState = ShowsGridState(
            isLoading = true,
            title = "",
            list = CommonFlow(flowOf())
        )
    }
}

sealed class ShowsGridAction : Action {
    object LoadTvShows : ShowsGridAction()
    data class Error(val message: String = "") : ShowsGridAction()
}

sealed class ShowsGridEffect : Effect {
    data class Error(val message: String = "") : ShowsGridEffect()
}
