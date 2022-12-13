package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetails
import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect

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
    val seasonsWithEpisodes: List<SeasonDetails>? = emptyList(),
    val showTitle: String = "",
)
