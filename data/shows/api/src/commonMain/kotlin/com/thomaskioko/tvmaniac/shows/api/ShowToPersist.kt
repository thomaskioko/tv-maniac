package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId

public data class ShowToPersist(
    val showId: Id<TraktId>?,
    val tmdbId: Id<TmdbId>,
    val name: String,
    val overview: String,
    val ratings: Double,
    val voteCount: Long,
    val language: String? = null,
    val year: String? = null,
    val status: String? = null,
    val genres: List<String>? = null,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val episodeNumbers: String? = null,
    val seasonNumbers: String? = null,
)
