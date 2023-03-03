package com.thomaskioko.tvmaniac.data.showdetails

import com.thomaskioko.tvmaniac.data.showdetails.model.Season
import com.thomaskioko.tvmaniac.data.showdetails.model.Show
import com.thomaskioko.tvmaniac.data.showdetails.model.Trailer


sealed interface ShowDetailsState {
    object Loading : ShowDetailsState
    data class ShowDetailsLoaded(
        val showState: ShowState,
        val similarShowsState: SimilarShowsState,
        val seasonState: SeasonState,
        val trailerState: TrailersState,
        val followShowState: FollowShowsState,
    ) : ShowDetailsState

    data class ShowDetailsError(val errorMessage: String) : ShowDetailsState
}

sealed interface ShowState {
    data class ShowLoaded(
        val show: Show,
    ) : ShowState

    data class ShowError(val errorMessage: String) : ShowState
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

    data class SeasonsError(val errorMessage: String) : SeasonState
}

sealed interface TrailersState {
    data class TrailersError(val errorMessage: String?) : TrailersState
    data class TrailersLoaded(
        val isLoading: Boolean,
        val hasWebViewInstalled: Boolean,
        val playerErrorMessage: String? = null,
        val trailersList: List<Trailer>,
    ) : TrailersState {
        companion object {
            const val playerErrorMessage: String =
                "Please make sure you have Android WebView installed or enabled."

            val EmptyTrailers = TrailersLoaded(
                isLoading = true,
                hasWebViewInstalled = false,
                playerErrorMessage = null,
                trailersList = emptyList()
            )
        }
    }
}


sealed interface SimilarShowsState {
    data class SimilarShowsError(val errorMessage: String) : SimilarShowsState
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
    data class FollowUpdateError(val errorMessage: String) : FollowShowsState
}


