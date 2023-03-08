package com.thomaskioko.tvmaniac.shared.domain.trailers.api

import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect


sealed class TrailerListAction : Action {
    data class LoadTrailers(
        val showId: Int
    ) : TrailerListAction()

    data class TrailerSelected(
        val trailerKey: String
    ) : TrailerListAction()

    data class VideoPlayerInitialized(
        val youtubePlayer: Any
    ) : TrailerListAction()


    data class Error(val message: String = "") : TrailerListAction()
}

sealed class TrailerListEffect : Effect {
    data class TrailerListError(val errorMessage: String = "") : TrailerListEffect()

}