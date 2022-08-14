package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.seasonepisodes.api.model.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow

sealed class SeasonsAction : Action {
    object LoadSeasons : SeasonsAction()
    data class Error(val message: String = "") : SeasonsAction()
}

sealed class SeasonsEffect : Effect {
    data class Error(val errorMessage: String = "") : SeasonsEffect()
}

sealed class SeasonsViewState

object Loading : SeasonsViewState()

data class SeasonsLoaded(
    val result : SeasonsResult
) : SeasonsViewState()

data class SeasonsResult(
    val seasonsWithEpisodes: List<SeasonWithEpisodes>? = emptyList(),
    val tvShow: TvShow = TvShow.EMPTY_SHOW,
)
