package com.thomaskioko.tvmaniac.presenter.showdetails.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class ShowDetailsModel(
    val tmdbId: Long,
    val title: String,
    val overview: String,
    val language: String?,
    val posterImageUrl: String?,
    val backdropImageUrl: String?,
    val year: String,
    val status: String?,
    val votes: Long = 0,
    val numberOfSeasons: Int = 0,
    val numberOfEpisodes: Long = 0,
    val rating: Double,
    val isInLibrary: Boolean,
    val hasWebViewInstalled: Boolean,
    val watchedEpisodesCount: Int = 0,
    val totalEpisodesCount: Int = 0,
    val watchProgress: Float = 0f,
    val genres: ImmutableList<String>,
    val providers: ImmutableList<ProviderModel>,
    val castsList: ImmutableList<CastModel>,
    val seasonsList: ImmutableList<SeasonModel>,
    val similarShows: ImmutableList<ShowModel>,
    val trailersList: ImmutableList<TrailerModel>,
    val selectedSeasonIndex: Int = 0,
) {
    public companion object {
        public val Empty: ShowDetailsModel = ShowDetailsModel(
            tmdbId = 0,
            title = "",
            overview = "",
            language = "",
            posterImageUrl = "",
            backdropImageUrl = "",
            year = "",
            status = "",
            votes = 0,
            rating = 0.0,
            isInLibrary = false,
            hasWebViewInstalled = false,
            genres = persistentListOf(),
            providers = persistentListOf(),
            castsList = persistentListOf(),
            seasonsList = persistentListOf(),
            similarShows = persistentListOf(),
            trailersList = persistentListOf(),
        )
    }
}
