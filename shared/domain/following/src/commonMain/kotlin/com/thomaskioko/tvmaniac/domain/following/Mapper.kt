package com.thomaskioko.tvmaniac.domain.following

import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows

fun List<SelectFollowedShows>?.followedShowList(): List<FollowedShow> {
    return this?.map {
        FollowedShow(
            traktId = it.id,
            tmdbId = it.tmdb_id,
            title = it.title,
            posterImageUrl = it.poster_url,
        )
    } ?: emptyList()
}