package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomakioko.tvmaniac.util.testing.FakeFormatterUtil
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowDetails
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test

class ShowDetailsMapperTest {

  private val showDetailsMapper = ShowDetailsMapper(FakeFormatterUtil())

  @Test
  fun `should return ShowDetails when TvshowDetails is provided`() {

    val expectedShowDetails = ShowDetails(
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
      genres = persistentListOf("1234"),
      isFollowed = false,
    )

    val result = showDetailsMapper.toShowDetails(tvShowDetails)

    result shouldBe expectedShowDetails
  }
}
