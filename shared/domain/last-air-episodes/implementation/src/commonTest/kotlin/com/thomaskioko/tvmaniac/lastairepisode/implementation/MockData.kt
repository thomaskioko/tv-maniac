package com.thomaskioko.tvmaniac.lastairepisode.implementation

import com.thomaskioko.tvmaniac.datasource.cache.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisode

fun makeLastEpisodeList(): List<AirEpisodesByShowId> = listOf(
    AirEpisodesByShowId(
        id = 126280,
        show_id = 84958,
        name = "Follow the Leader?",
        overview = "A woman's daring sexual past collides with her married-with-kids " +
            "present when the bad-boy ex she can't stop fantasizing about crashes " +
            "back into her life.",
        title = "Latest",
        air_date = "2014-03-28",
        episode_number = 3,
        season_number = 1,
        still_path = null,
        vote_count = 0,
        vote_average = 12.2,
        id_ = 126280,
        title_ = "Sex/Life",
        description = "A woman's daring sexual past collides with her married-with-kids " +
            "present when the bad-boy ex she can't stop fantasizing about crashes " +
            "back into her life.",
        poster_image_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdrop_image_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        language = "en",
        votes = 4958,
        vote_average_ = 8.1,
        genre_ids = listOf(35, 18),
        year = "2019",
        status = "Ended",
        popularity = 24.4848,
        following = false,
        number_of_episodes = 30,
        number_of_seasons = 2
    ),
    AirEpisodesByShowId(
        id = 12628,
        show_id = 84958,
        name = "Follow the Leader?",
        overview = "A woman's daring sexual past collides with her married-with-kids " +
            "present when the bad-boy ex she can't stop fantasizing about crashes " +
            "back into her life.",
        title = "Coming soon",
        air_date = "2014-03-28",
        episode_number = 3,
        season_number = 1,
        still_path = null,
        vote_count = 0,
        vote_average = 12.2,
        id_ = 126280,
        title_ = "Sex/Life",
        description = "A woman's daring sexual past collides with her married-with-kids " +
            "present when the bad-boy ex she can't stop fantasizing about crashes " +
            "back into her life.",
        poster_image_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdrop_image_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        language = "en",
        votes = 4958,
        vote_average_ = 8.1,
        genre_ids = listOf(35, 18),
        year = "2019",
        status = "Ended",
        popularity = 24.4848,
        following = false,
        number_of_episodes = 30,
        number_of_seasons = 2
    )
)

fun makeLastAirEpisode(): List<LastAirEpisode> = listOf(
    LastAirEpisode(
        id = 126280,
        name = "S1.E03 • Follow the Leader?",
        overview = "A woman's daring sexual past collides with her married-with-kids " +
            "present when the bad-boy ex she can't stop fantasizing about crashes " +
            "back into her life.",
        title = "Latest",
        airDate = "2014-03-28",
        episodeNumber = 3,
        seasonNumber = 1,
        posterPath = null,
        voteCount = 0,
        voteAverage = 12.2,

    ),
    LastAirEpisode(
        id = 12628,
        name = "S1.E03 • Follow the Leader?",
        overview = "A woman's daring sexual past collides with her married-with-kids " +
            "present when the bad-boy ex she can't stop fantasizing about crashes " +
            "back into her life.",
        title = "Coming soon",
        airDate = "2014-03-28",
        episodeNumber = 3,
        seasonNumber = 1,
        posterPath = null,
        voteCount = 0,
        voteAverage = 12.2,
    )
)
