package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.core.db.RecommendedShows
import com.thomaskioko.tvmaniac.core.db.ShowSeasons
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.core.db.WatchProviders
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowDetails
import kotlinx.collections.immutable.persistentListOf

val showDetailsContent =
  ShowDetailsContent(
    showDetails =
      ShowDetails(
        tmdbId = 849583,
        title = "Loki",
        overview =
          "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        language = "en",
        year = "2021-06-09",
        status = "Ended",
        votes = 1L,
        rating = 8.0,
        genres = persistentListOf("1234"),
      ),
    showInfo =
      ShowDetailsContent.ShowInfoContent(
        seasonsList = persistentListOf(),
        similarShows = persistentListOf(),
        trailersList = persistentListOf(),
        providers = persistentListOf(),
        castsList = persistentListOf(),
        recommendedShowList = persistentListOf(),
        hasWebViewInstalled = false,
      ),
    errorMessage = null,
    isUpdating = false,
  )

val similarShowList =
  listOf(
    SimilarShows(
      id = Id(184958),
      name = "Loki",
      poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
      backdrop_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
      in_library = 0,
    ),
  )

val seasons =
  listOf(
    ShowSeasons(
      season_id = Id(84958),
      show_id = Id(114355),
      season_title = "Season 1",
      season_number = 1,
    ),
  )

val tvShowDetails =
  TvshowDetails(
    id = Id(849583),
    name = "Loki",
    overview =
      "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
    language = "en",
    first_air_date = "2021-06-09",
    last_air_date = "2021-06-09",
    popularity = 1.0,
    vote_average = 8.0,
    vote_count = 1,
    status = "Ended",
    poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    backdrop_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    genre_list = "1234",
    in_library = 0,
  )

val recommendedShowList =
  listOf(
    RecommendedShows(
      id = Id(184958),
      name = "Loki",
      poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
      backdrop_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
      in_library = 0,
    )
  )

val watchProviderList =
  listOf(
    WatchProviders(
      id = Id(184958),
      name = "Netflix",
      logo_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    )
  )
