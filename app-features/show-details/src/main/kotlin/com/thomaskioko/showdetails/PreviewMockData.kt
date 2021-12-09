package com.thomaskioko.showdetails

import com.thomaskioko.tvmaniac.presentation.model.Episode
import com.thomaskioko.tvmaniac.presentation.model.GenreModel
import com.thomaskioko.tvmaniac.presentation.model.Season
import com.thomaskioko.tvmaniac.presentation.model.TvShow

val detailUiState = ShowDetailViewState(
    isLoading = false,
    errorMessage = "",
    tvShow = TvShow(
        id = 84958,
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
        averageVotes = 8.1,
        genreIds = listOf(18, 10765),
    ),
    tvSeasons = getTvSeasons(),
    genreList = getGenres(),
    episodesViewState = EpisodesViewState(
        isLoading = false,
        episodeList = getEpisodeList()
    ),
    trailerViewState = TrailersViewState()

)

private fun getTvSeasons() = listOf(
    Season(
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
    GenreModel(
        id = 18,
        name = "Sci-Fi"
    ),
    GenreModel(
        id = 10765,
        name = "Action"
    )
)

fun getEpisodeList() = listOf(
    Episode(
        id = 2534997,
        seasonId = 114355,
        name = "Glorious Purpose",
        overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
        imageUrl = "https://image.tmdb.org/t/p/original/gxh0k3aADsYkt9tgkfm2kGn2qQj.jpg",
        voteCount = 42,
        voteAverage = 6.429,
        seasonNumber = 1,
        episodeNumber = "01"
    ),
    Episode(
        id = 2927202,
        seasonId = 114355,
        name = "The Variant",
        overview = "Mobius puts Loki to work, but not everyone at TVA is thrilled about the God of Mischief's presence.",
        imageUrl = "https://image.tmdb.org/t/p/original/gqpcfkdmSsm6xiX2EsLkwUvA8g8.jpg",
        voteCount = 23,
        voteAverage = 7.6,
        seasonNumber = 1,
        episodeNumber = "02"
    )
)
