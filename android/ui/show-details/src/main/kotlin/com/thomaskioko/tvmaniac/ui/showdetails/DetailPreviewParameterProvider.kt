package com.thomaskioko.tvmaniac.ui.showdetails

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsContent
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowInfoState
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Casts
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Providers
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowDetails
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import kotlinx.collections.immutable.persistentListOf

val showDetailsContent =
  ShowDetailsContent(
    showDetails =
      ShowDetails(
        title = "Loki",
        overview =
          "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
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
        genres = persistentListOf("Horror", "Action"),
        status = "Returning Series",
        year = "2024",
      ),
    showInfo =
      ShowInfoState.Loaded(
        seasonsList =
          persistentListOf(
            Season(
              seasonId = 114355,
              tvShowId = 84958,
              name = "Season 1",
              seasonNumber = 1,
            ),
          ),
        trailersList =
          persistentListOf(
            Trailer(
              showId = 1232,
              key = "",
              name = "",
              youtubeThumbnailUrl = "",
            ),
            Trailer(
              showId = 1232,
              key = "",
              name = "",
              youtubeThumbnailUrl = "",
            ),
          ),
        similarShows =
          persistentListOf(
            Show(
              tmdbId = 1232,
              title = "Loki",
              posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
              backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
              isInLibrary = false,
            )
          ),
        recommendedShowList =
          persistentListOf(
            Show(
              tmdbId = 1232,
              title = "Loki",
              posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
              backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
              isInLibrary = false,
            )
          ),
        providers =
          persistentListOf(
            Providers(id = 1L, logoUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg", name = "Netflix")
          ),
        castsList =
          persistentListOf(
            Casts(id = 1L, name = "Character", profileUrl = null, characterName = "Starring")
          ),
        hasWebViewInstalled = false,
      ),
    errorMessage = null,
  )

val showDetailsContentWithEmptyInfo = showDetailsContent.copy(showInfo = ShowInfoState.Empty)

val showDetailsContentWithError = showDetailsContent.copy(showInfo = ShowInfoState.Error)

class DetailPreviewParameterProvider : PreviewParameterProvider<ShowDetailsContent> {
  override val values: Sequence<ShowDetailsContent>
    get() {
      return sequenceOf(
        showDetailsContent,
        showDetailsContentWithEmptyInfo,
        showDetailsContentWithError
      )
    }
}
