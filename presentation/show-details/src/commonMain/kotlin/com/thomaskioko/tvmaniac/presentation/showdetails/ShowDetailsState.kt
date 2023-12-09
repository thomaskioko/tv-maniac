package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsState.SeasonsContent.Companion.EMPTY_SEASONS
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsState.SimilarShowsContent.Companion.EMPTY_SIMILAR_SHOWS
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsState.TrailersContent.Companion.EMPTY_TRAILERS
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ShowDetailsState(
    val show: Show,
    val isLoading: Boolean = false,
    val errorMessage: String?,
    val similarShowsContent: SimilarShowsContent,
    val seasonsContent: SeasonsContent,
    val trailersContent: TrailersContent,
) {
    companion object {
        val EMPTY_DETAIL_STATE = ShowDetailsState(
            show = Show.EMPTY_SHOW,
            errorMessage = null,
            similarShowsContent = EMPTY_SIMILAR_SHOWS,
            seasonsContent = EMPTY_SEASONS,
            trailersContent = EMPTY_TRAILERS,
        )
    }

    data class SeasonsContent(
        val isLoading: Boolean,
        val seasonsList: ImmutableList<Season>,
        val errorMessage: String? = null,
    ) {
        companion object {
            val EMPTY_SEASONS = SeasonsContent(
                errorMessage = null,
                isLoading = false,
                seasonsList = persistentListOf(),
            )
        }
    }

    data class TrailersContent(
        val isLoading: Boolean,
        val hasWebViewInstalled: Boolean,
        val playerErrorMessage: String? = null,
        val trailersList: ImmutableList<Trailer>,
        val errorMessage: String? = null,
    ) {
        companion object {
            const val playerErrorMessage: String =
                "Please make sure you have Android WebView installed or enabled."

            val EMPTY_TRAILERS = TrailersContent(
                isLoading = true,
                hasWebViewInstalled = false,
                playerErrorMessage = null,
                trailersList = persistentListOf(),
                errorMessage = null,
            )
        }
    }

    data class SimilarShowsContent(
        val isLoading: Boolean,
        val similarShows: ImmutableList<Show>,
        val errorMessage: String? = null,
    ) {
        companion object {
            val EMPTY_SIMILAR_SHOWS = SimilarShowsContent(
                isLoading = true,
                similarShows = persistentListOf(),
                errorMessage = null,
            )
        }
    }
}
