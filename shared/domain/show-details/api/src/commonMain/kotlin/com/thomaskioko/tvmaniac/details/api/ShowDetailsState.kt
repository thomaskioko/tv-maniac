package com.thomaskioko.tvmaniac.details.api

import com.thomaskioko.tvmaniac.details.api.model.Season
import com.thomaskioko.tvmaniac.details.api.model.Show
import com.thomaskioko.tvmaniac.details.api.model.Trailer


sealed interface ShowDetailsState {
    object Loading : ShowDetailsState
    data class ShowDetailsLoaded(
        val show: Show,
        val similarShowsState: SimilarShowsState,
        val seasonState: SeasonState,
        val trailerState: TrailersState,
        val followShowState: FollowShowsState,
    ) : ShowDetailsState

    data class ShowDetailsError(val errorMessage: String?) : ShowDetailsState
}


sealed interface SeasonState {
    data class SeasonsLoaded(
        val isLoading: Boolean,
        val seasonsList: List<Season>,
    ) : SeasonState {
        companion object {
            val EmptySeasons = SeasonsLoaded(
                isLoading = true,
                seasonsList = emptyList()
            )
        }
    }

    data class SeasonsError(val errorMessage: String?) : SeasonState
}

sealed interface TrailersState {
    data class TrailersError(val errorMessage: String?) : TrailersState
    data class TrailersLoaded(
        val isLoading: Boolean,
        val hasWebViewInstalled: Boolean,
        val playerErrorMessage: String?,
        val trailersList: List<Trailer>,
    ) : TrailersState {
        companion object {
            val EmptyTrailers = TrailersLoaded(
                isLoading = true,
                hasWebViewInstalled = false,
                playerErrorMessage = null,
                trailersList = emptyList()
            )

            const val playerErrorMessage: String =
                "Please make sure you have Android WebView installed or enabled."
        }
    }
}


sealed interface SimilarShowsState {
    data class SimilarShowsError(val errorMessage: String?) : SimilarShowsState
    data class SimilarShowsLoaded(
        val isLoading: Boolean,
        val similarShows: List<Show>,
    ) : SimilarShowsState {
        companion object {
            val EmptyShows = SimilarShowsLoaded(
                isLoading = true,
                similarShows = emptyList()
            )
        }
    }
}


sealed interface FollowShowsState {
    object Idle : FollowShowsState
    data class FollowUpdateError(val errorMessage: String?) : FollowShowsState
}


