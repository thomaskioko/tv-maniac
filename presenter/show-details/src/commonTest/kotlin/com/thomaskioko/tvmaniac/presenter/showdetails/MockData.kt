package com.thomaskioko.tvmaniac.presenter.showdetails

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.db.TvshowDetails
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.episodes.api.model.ContinueTrackingResult
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowDetailsModel
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

val showDetailsContent = ShowDetailsContent(
    showDetails = ShowDetailsModel.Empty.copy(
        tmdbId = 849583,
        title = "Loki",
        overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        language = "en",
        year = "2021-06-09",
        status = "Ended",
        votes = 1L,
        rating = 8.0,
        genres = persistentListOf("Action", "Adventure", "Sci-Fi"),
        isInLibrary = false,
    ),
    message = null,
)

val similarShowList = listOf(
    SimilarShows(
        show_tmdb_id = Id(184958),
        name = "Loki",
        poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdrop_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        in_library = 0,
        show_trakt_id = Id(18495),
        similar_show_trakt_id = Id(18495),
    ),
)

val seasons = listOf(
    ShowSeasons(
        season_id = Id(84958),
        show_trakt_id = Id(114355),
        season_title = "Season 1",
        season_number = 1,
    ),
)

val tvShowDetails = TvshowDetails(
    trakt_id = Id(849583),
    tmdb_id = Id(849583),
    name = "Loki",
    overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
    language = "en",
    year = "2021-06-09",
    ratings = 8.0,
    status = "Ended",
    vote_count = 1,
    poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    backdrop_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    genres = listOf("Action", "Adventure", "Sci-Fi"),
    season_numbers = "2",
    in_library = 0,
)

val watchProviderList = listOf(
    WatchProviders(
        provider_id = Id(184958),
        name = "Netflix",
        logo_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        tmdb_id = Id(18495),
    ),
)

val testEpisodeDetails = EpisodeDetails(
    id = 1001L,
    seasonId = 101L,
    name = "Pilot",
    seasonNumber = 1L,
    episodeNumber = 1L,
    runtime = 45L,
    overview = "The first episode",
    voteAverage = 8.5,
    voteCount = 100L,
    stillPath = "/episode1.jpg",
    airDate = "2023-01-01",
    isWatched = false,
    daysUntilAir = null,
    hasAired = true,
)

val testContinueTrackingResult = ContinueTrackingResult(
    episodes = listOf(
        testEpisodeDetails,
        testEpisodeDetails.copy(id = 1002L, episodeNumber = 2L, name = "Episode 2"),
        testEpisodeDetails.copy(id = 1003L, episodeNumber = 3L, name = "Episode 3"),
    ).toImmutableList(),
    firstUnwatchedIndex = 0,
    currentSeasonNumber = 1L,
    currentSeasonId = 101L,
)

val testShowWatchProgress = ShowWatchProgress(
    showTraktId = 84958L,
    watchedCount = 5,
    totalCount = 10,
)

val testSeasonsWithProgress = listOf(
    ShowSeasons(
        season_id = Id(101),
        show_trakt_id = Id(84958),
        season_title = "Season 1",
        season_number = 1,
    ),
    ShowSeasons(
        season_id = Id(102),
        show_trakt_id = Id(84958),
        season_title = "Season 2",
        season_number = 2,
    ),
)

val testSeasonWatchProgress = listOf(
    SeasonWatchProgress(
        showTraktId = 84958L,
        seasonNumber = 1L,
        watchedCount = 8,
        totalCount = 10,
    ),
    SeasonWatchProgress(
        showTraktId = 84958L,
        seasonNumber = 2L,
        watchedCount = 3,
        totalCount = 12,
    ),
)

val testPartialSeasonProgress = listOf(
    SeasonWatchProgress(
        showTraktId = 84958L,
        seasonNumber = 1L,
        watchedCount = 5,
        totalCount = 10,
    ),
)

val testCompletedSeasonProgress = listOf(
    SeasonWatchProgress(
        showTraktId = 84958L,
        seasonNumber = 1L,
        watchedCount = 10,
        totalCount = 10,
    ),
)
