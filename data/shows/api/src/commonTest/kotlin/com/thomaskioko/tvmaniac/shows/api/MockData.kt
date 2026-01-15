package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.db.Tvshow

internal object MockData {

    fun getShow() = Tvshow(
        trakt_id = Id<TraktId>(84958),
        tmdb_id = Id<TmdbId>(84958),
        name = "Loki",
        overview = "After stealing the Tesseract during the events of Avengers: Endgame, " +
            "an alternate version of Loki is brought to the mysterious Time Variance " +
            "Authority, a bureaucratic organization that exists outside of time and " +
            "space and monitors the timeline. They give Loki a choice: face being " +
            "erased from existence due to being a time variant or help fix " +
            "the timeline and stop a greater threat.",
        language = "en",
        vote_count = 4958,
        genres = listOf("Action", "Adventure", "Fantasy", "Science Fiction"),
        year = "2019",
        status = "Ended",
        poster_path = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
        backdrop_path = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
        episode_numbers = "10",
        season_numbers = "1",
        ratings = 8.1,
    )

    fun showList() = listOf(
        Tvshow(
            trakt_id = Id<TraktId>(84958),
            tmdb_id = Id<TmdbId>(84958),
            name = "Loki",
            overview = "After stealing the Tesseract during the events of Avengers: Endgame, " +
                "an alternate version of Loki is brought to the mysterious Time Variance " +
                "Authority, a bureaucratic organization that exists outside of time and " +
                "space and monitors the timeline. They give Loki a choice: face being " +
                "erased from existence due to being a time variant or help fix " +
                "the timeline and stop a greater threat.",
            language = "en",
            vote_count = 4958,
            genres = listOf("Action", "Adventure", "Fantasy", "Science Fiction"),
            year = "2019",
            status = "Ended",
            poster_path = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
            backdrop_path = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
            episode_numbers = "10",
            season_numbers = "1",
            ratings = 8.1,
        ),
        Tvshow(
            trakt_id = Id<TraktId>(126280),
            tmdb_id = Id<TmdbId>(126280),
            name = "Sex/Life",
            overview = "A woman's daring sexual past collides with her married-with-kids " +
                "present when the bad-boy ex she can't stop fantasizing about crashes " +
                "back into her life.",
            language = "en",
            vote_count = 4958,
            genres = listOf("Drama", "Romance"),
            year = "2019",
            status = "Ended",
            poster_path = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
            backdrop_path = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
            episode_numbers = "10",
            season_numbers = "1",
            ratings = 8.1,
        ),
    )
}
