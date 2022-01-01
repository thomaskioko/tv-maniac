package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.SelectShows
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.presentation.model.ShowUiModel

fun List<Show>.toTvShowList(): List<ShowUiModel> {
    return map { it.toTvShow() }
}

fun Show.toTvShow(): ShowUiModel {
    return ShowUiModel(
        id = id.toInt(),
        title = title,
        overview = description,
        language = language,
        posterImageUrl = poster_image_url,
        backdropImageUrl = backdrop_image_url,
        votes = votes.toInt(),
        averageVotes = vote_average,
        genreIds = genre_ids,
        year = year,
        status = status,
        isInWatchlist = is_watchlist
    )
}

fun List<SelectShows>.toShowList(): List<Show> {
    return map { it.toShow() }
}

fun SelectShows.toShow(): Show {
    return Show(
        id = id,
        title = title,
        description = description,
        language = language,
        poster_image_url = poster_image_url,
        backdrop_image_url = backdrop_image_url,
        votes = votes,
        vote_average = vote_average,
        genre_ids = genre_ids,
        year = year,
        status = status,
        is_watchlist = is_watchlist,
        popularity = popularity,
        season_ids = season_ids,
    )
}
