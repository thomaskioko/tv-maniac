package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsLoaded.SeasonsContent.Companion.EMPTY_SEASONS
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsLoaded.SimilarShowsContent.Companion.EMPTY_SIMILAR_SHOWS
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsLoaded.TrailersContent.Companion.EMPTY_TRAILERS
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer

sealed interface ShowDetailsState

data class ShowDetailsLoaded(
    val show: Show,
    val isLoading: Boolean = false,
    val errorMessage: String?,
    val similarShowsContent: SimilarShowsContent,
    val seasonsContent: SeasonsContent,
    val trailersContent: TrailersContent,
) : ShowDetailsState {
    companion object {
        val EMPTY_DETAIL_STATE = ShowDetailsLoaded(
            show = Show.EMPTY_SHOW,
            errorMessage = null,
            similarShowsContent = EMPTY_SIMILAR_SHOWS,
            seasonsContent = EMPTY_SEASONS,
            trailersContent = EMPTY_TRAILERS,
        )
    }

    data class SeasonsContent(
        val isLoading: Boolean,
        val seasonsList: List<Season>,
        val errorMessage: String? = null,
    ) {
        companion object {
            val EMPTY_SEASONS = SeasonsContent(
                errorMessage = null,
                isLoading = false,
                seasonsList = emptyList(),
            )
        }
    }

    data class TrailersContent(
        val isLoading: Boolean,
        val hasWebViewInstalled: Boolean,
        val playerErrorMessage: String? = null,
        val trailersList: List<Trailer>,
        val errorMessage: String? = null,
    ) {
        companion object {
            const val playerErrorMessage: String =
                "Please make sure you have Android WebView installed or enabled."

            val EMPTY_TRAILERS = TrailersContent(
                isLoading = true,
                hasWebViewInstalled = false,
                playerErrorMessage = null,
                trailersList = emptyList(),
                errorMessage = null,
            )
        }
    }

    data class SimilarShowsContent(
        val isLoading: Boolean,
        val similarShows: List<Show>,
        val errorMessage: String? = null,
    ) {
        companion object {
            val EMPTY_SIMILAR_SHOWS = SimilarShowsContent(
                isLoading = true,
                similarShows = emptyList(),
                errorMessage = null,
            )
        }
    }
}
