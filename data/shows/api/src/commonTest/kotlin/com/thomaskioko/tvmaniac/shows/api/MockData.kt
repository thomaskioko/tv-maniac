package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Tvshow

internal object MockData {

    fun getShow() = Tvshow(
        id = Id(84958),
        name = "Loki",
        overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
            "an alternate version of Loki is brought to the mysterious Time Variance " +
            "Authority, a bureaucratic organization that exists outside of time and " +
            "space and monitors the timeline. They give Loki a choice: face being " +
            "erased from existence due to being a “time variant”or help fix " +
            "the timeline and stop a greater threat.",
        language = "en",
        vote_count = 4958,
        popularity = 8.1,
        genre_ids = listOf(12, 14, 16, 18, 27, 28),
        first_air_date = "2019",
        status = "Ended",
        poster_path = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
        backdrop_path = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
        episode_numbers = "10",
        season_numbers = "1",
        vote_average = 8.1,
        last_air_date = "2019",
    )

    fun showList() = listOf(
        Tvshow(
            id = Id(84958),
            name = "Loki",
            overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                "an alternate version of Loki is brought to the mysterious Time Variance " +
                "Authority, a bureaucratic organization that exists outside of time and " +
                "space and monitors the timeline. They give Loki a choice: face being " +
                "erased from existence due to being a “time variant”or help fix " +
                "the timeline and stop a greater threat.",
            language = "en",
            vote_count = 4958,
            popularity = 8.1,
            genre_ids = listOf(12, 14, 16, 18, 27, 28),
            first_air_date = "2019",
            status = "Ended",
            poster_path = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
            backdrop_path = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
            episode_numbers = "10",
            season_numbers = "1",
            vote_average = 8.1,
            last_air_date = "2019",
        ),
        Tvshow(
            id = Id(126280),
            name = "Sex/Life",
            overview = "A woman's daring sexual past collides with her married-with-kids " +
                "present when the bad-boy ex she can't stop fantasizing about crashes " +
                "back into her life.",
            language = "en",
            vote_count = 4958,
            popularity = 8.1,
            genre_ids = listOf(12, 14, 16, 18, 27, 28),
            first_air_date = "2019",
            status = "Ended",
            poster_path = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
            backdrop_path = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
            episode_numbers = "10",
            season_numbers = "1",
            vote_average = 8.1,
            last_air_date = "2019",
        ),
    )
}
