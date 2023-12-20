package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.core.db.ShowSeasons
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowDetails
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import kotlinx.collections.immutable.persistentListOf

val similarShow = ShowDetails(
    tmdbId = 849583,
    title = "Loki",
    posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
)

val similarSimilarShows = persistentListOf(
    Show(
        tmdbId = 284958,
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        isInLibrary = false,
    ),
)

val showDetailsLoaded = ShowDetailsState(
    showDetails = ShowDetails(
        tmdbId = 849583,
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    ),
    seasonsList = persistentListOf(),
    similarShows = persistentListOf(),
    trailersList = persistentListOf(),
    errorMessage = null,
    isLoading = false,
    providers = persistentListOf(),
    castsList = persistentListOf(),
    recommendedShowList = persistentListOf(),
    hasWebViewInstalled = false,
)

val seasonPersistentList = persistentListOf(
    Season(
        seasonId = 84958,
        tvShowId = 114355,
        name = "Season 1",
        seasonNumber = 0,
    ),
)

val trailerPersistentList = persistentListOf(
    Trailer(
        showId = 84958,
        key = "Fd43V",
        name = "Some title",
        youtubeThumbnailUrl = "https://i.ytimg.com/vi/Fd43V/hqdefault.jpg",
    ),
)

val similarShowList = similarSimilarShows

val similarShowResult = listOf(
    SimilarShows(
        id = Id(184958),
        name = "Loki",
        poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdrop_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        in_library = 0,
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
