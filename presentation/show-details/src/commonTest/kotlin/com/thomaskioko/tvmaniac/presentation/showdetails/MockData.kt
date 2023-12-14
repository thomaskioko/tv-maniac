package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.core.db.ShowSeasons
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowDetails
import com.thomaskioko.tvmaniac.presentation.showdetails.model.SimilarShow
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import kotlinx.collections.immutable.persistentListOf

val similarShow = ShowDetails(
    tmdbId = 849583,
    title = "Loki",
    posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
)

val similarSimilarShows = persistentListOf(
    SimilarShow(
        tmdbId = 284958,
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    ),
)

val showDetailsLoaded = ShowDetailsState(
    showDetails = ShowDetails(
        tmdbId = 849583,
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    ),
    seasonsContent = ShowDetailsState.SeasonsContent(
        isLoading = true,
        seasonsList = persistentListOf(),
    ),
    similarShowsContent = ShowDetailsState.SimilarShowsContent(
        isLoading = true,
        similarSimilarShows = persistentListOf(),
    ),
    trailersContent = ShowDetailsState.TrailersContent(
        isLoading = true,
        hasWebViewInstalled = false,
        playerErrorMessage = null,
        trailersList = persistentListOf(),
    ),
    errorMessage = null,
)
val seasonsShowDetailsLoaded = ShowDetailsState.SeasonsContent(
    isLoading = false,
    seasonsList = persistentListOf(
        Season(
            seasonId = 84958,
            tvShowId = 114355,
            name = "Season 1",
        ),
    ),
)

val trailerShowDetailsLoaded = ShowDetailsState.TrailersContent(
    isLoading = false,
    hasWebViewInstalled = false,
    playerErrorMessage = null,
    trailersList = persistentListOf(
        Trailer(
            showId = 84958,
            key = "Fd43V",
            name = "Some title",
            youtubeThumbnailUrl = "https://i.ytimg.com/vi/Fd43V/hqdefault.jpg",
        ),
    ),
)

val similarShowLoaded = ShowDetailsState.SimilarShowsContent(
    isLoading = false,
    similarSimilarShows = similarSimilarShows,
)

val similarShowResult = listOf(
    SimilarShows(
        id = Id(184958),
        name = "Loki",
        poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdrop_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    ),
)

val seasons = listOf(
    ShowSeasons(
        season_id = Id(84958),
        show_id = Id(114355),
        season_title = "Season 1",
        season_number = 1,
    ),
)
