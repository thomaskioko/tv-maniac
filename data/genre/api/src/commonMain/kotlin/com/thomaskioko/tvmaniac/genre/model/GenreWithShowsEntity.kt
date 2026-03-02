package com.thomaskioko.tvmaniac.genre.model

import com.thomaskioko.tvmaniac.genre.model.TraktGenreEntity
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity

public data class GenreWithShowsEntity(
    val genre: TraktGenreEntity,
    val shows: List<ShowEntity>,
)
