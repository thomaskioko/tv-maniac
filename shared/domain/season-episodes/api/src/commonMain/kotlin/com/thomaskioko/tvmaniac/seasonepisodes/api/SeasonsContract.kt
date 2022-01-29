package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.discover.api.model.TvShow
import com.thomaskioko.tvmaniac.seasonepisodes.api.model.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.shared.core.store.Action
import com.thomaskioko.tvmaniac.shared.core.store.Effect
import com.thomaskioko.tvmaniac.shared.core.store.State

sealed class SeasonsAction : Action {
    object LoadSeasons : SeasonsAction()
    object LoadShowDetails : SeasonsAction()
    data class Error(val message: String = "") : SeasonsAction()
}

sealed class SeasonsEffect : Effect {
    data class Error(val errorMessage: String = "") : SeasonsEffect()
}

data class SeasonsViewState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val seasonsWithEpisodes: List<SeasonWithEpisodes>? = emptyList(),
    val tvShow: TvShow = TvShow.EMPTY_SHOW,
) : State {
    companion object {
        val Empty = SeasonsViewState()
    }
}
