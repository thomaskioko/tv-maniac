package com.thomaskioko.tvmaniac.interactors

import com.kuuurt.paging.multiplatform.PagingData
import com.thomaskioko.tvmaniac.shared.core.store.Action
import com.thomaskioko.tvmaniac.shared.core.store.Effect
import com.thomaskioko.tvmaniac.shared.core.store.State
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow
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
