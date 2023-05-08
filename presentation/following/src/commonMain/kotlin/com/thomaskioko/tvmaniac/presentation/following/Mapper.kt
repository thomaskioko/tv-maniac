package com.thomaskioko.tvmaniac.presentation.following

import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows

fun List<SelectFollowedShows>?.followedShowList(): List<FollowingShow> {
    return this?.map {
        FollowingShow(
            traktId = it.id,
            tmdbId = it.tmdb_id,
            title = it.title,
            posterImageUrl = it.poster_url,
        )
    } ?: emptyList()
}
