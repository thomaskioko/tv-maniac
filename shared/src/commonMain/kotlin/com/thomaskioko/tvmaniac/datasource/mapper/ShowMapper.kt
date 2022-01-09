package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.discover.api.model.ShowUiModel

fun List<Show>.toTvShowList(): List<ShowUiModel> {
    return map {
        ShowUiModel(
            id = it.id.toInt(),
            title = it.title,
            overview = it.description,
            language = it.language,
            posterImageUrl = it.poster_image_url,
            backdropImageUrl = it.backdrop_image_url,
            votes = it.votes.toInt(),
            averageVotes = it.vote_average,
            genreIds = it.genre_ids,
            year = it.year,
            status = it.status,
            isInWatchlist = it.is_watchlist
        )
    }
}
