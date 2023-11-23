package com.thomaskioko.tvmaniac.seasondetails.testing

import com.thomaskioko.tvmaniac.core.db.SeasonEpisodeDetailsById
import com.thomaskioko.tvmaniac.db.Id

val SeasonWithEpisodeList = listOf(
    SeasonEpisodeDetailsById(
        show_id = Id(84958),
        season_id = Id(12343),
        show_title = "Loki",
        overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
        runtime = 45,
        season_number = 0,
        episode_count = 1,
        season_title = "Season 01",
        season_overview = "The journey to reunite the Ingham family continues as they travel to the USA.",
        ratings = 4.5,
        episode_number = "01",
        votes = 4958,
        episode_image_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        episode_title = "Some title",
        episode_season_id = Id(12345),
        episode_id = Id(12345),
    ),
)
