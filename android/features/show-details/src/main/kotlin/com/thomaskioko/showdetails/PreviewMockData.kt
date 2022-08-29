package com.thomaskioko.showdetails

import com.thomaskioko.tvmaniac.details.api.presentation.ShowDetailViewState
import com.thomaskioko.tvmaniac.genre.api.GenreUIModel
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisode
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.model.Trailer
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow

val detailUiState = ShowDetailViewState(
    tvShow = TvShow(
        traktId = 84958,
        title = "Loki",
        overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                "an alternate version of Loki is brought to the mysterious Time Variance " +
                "Authority, a bureaucratic organization that exists outside of time and " +
                "space and monitors the timeline. They give Loki a choice: face being " +
                "erased from existence due to being a “time variant”or help fix " +
                "the timeline and stop a greater threat.",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        language = "en",
        votes = 4958,
        rating = 8.1,
        genres = listOf("Horror", "Action"),
        status = "Returning Series",
        year = "2024"
    ),
    tvSeasonUiModels = getTvSeasons(),
    lastAirEpList = getEpisodeList(),
    trailersList = listOf(
        Trailer(
            showId = 1232,
            key = "",
            name = "",
            youtubeThumbnailUrl = ""
        ),
        Trailer(
            showId = 1232,
            key = "",
            name = "",
            youtubeThumbnailUrl = ""
        ),
    )
)

private fun getTvSeasons() = listOf(
    SeasonUiModel(
        seasonId = 114355,
        tvShowId = 84958,
        name = "Season 1",
        overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                "an alternate version of Loki is brought to the mysterious Time Variance " +
                "Authority, a bureaucratic organization that exists outside of time and " +
                "space and monitors the timeline. They give Loki a choice: face being " +
                "erased from existence due to being a “time variant”or help fix " +
                "the timeline and stop a greater threat.",
        seasonNumber = 1,
        episodeCount = 6
    )
)

fun getGenres() = listOf(
    GenreUIModel(
        id = 18,
        name = "Sci-Fi"
    ),
    GenreUIModel(
        id = 10765,
        name = "Action"
    )
)

fun getEpisodeList() = listOf(
    LastAirEpisode(
        id = 2534997,
        name = "Glorious Purpose",
        overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time" +
                " Variance Authority.",
        posterPath = "https://image.tmdb.org/t/p/original/gxh0k3aADsYkt9tgkfm2kGn2qQj.jpg",
        voteCount = 42,
        voteAverage = 6.429,
        seasonNumber = 1,
        episodeNumber = 1,
        airDate = "Wed, Apr 7, 2021",
        title = "Latest"
    ),
    LastAirEpisode(
        id = 2927202,
        name = "The Variant",
        overview = "Mobius puts Loki to work, but not everyone at TVA is thrilled about the God of " +
                "Mischief's presence.",
        posterPath = "https://image.tmdb.org/t/p/original/gqpcfkdmSsm6xiX2EsLkwUvA8g8.jpg",
        voteCount = 23,
        voteAverage = 7.6,
        seasonNumber = 1,
        episodeNumber = 1,
        airDate = "Wed, Apr 13, 2021",
        title = "Upcoming"
    )
)

val showList = listOf(
    TvShow(
        traktId = 84958,
        title = "Loki",
        overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                "an alternate version of Loki is brought to the mysterious Time Variance " +
                "Authority, a bureaucratic organization that exists outside of time and " +
                "space and monitors the timeline. They give Loki a choice: face being " +
                "erased from existence due to being a “time variant”or help fix " +
                "the timeline and stop a greater threat.",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        language = "en",
        votes = 4958,
        rating = 8.1,
        genres = listOf("Horror", "Action"),
        status = "Returning Series",
        year = "2024"
    ),
    TvShow(
        traktId = 84958,
        title = "Loki",
        overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                "an alternate version of Loki is brought to the mysterious Time Variance " +
                "Authority, a bureaucratic organization that exists outside of time and " +
                "space and monitors the timeline. They give Loki a choice: face being " +
                "erased from existence due to being a “time variant”or help fix " +
                "the timeline and stop a greater threat.",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        language = "en",
        votes = 4958,
        rating = 8.1,
        genres = listOf("Horror", "Action"),
        status = "Returning Series",
        year = "2024"
    )
)
