package com.thomaskioko.tvmaniac.shows.api.model

data class TvShow(
    val traktId: Int = 0,
    val tmdbId: Int? = 0,
    val title: String = "",
    val overview: String = "",
    val language: String? = null,
    val posterImageUrl: String? = null,
    val backdropImageUrl: String? = null,
    val year: String = "",
    val status: String? = null,
    val votes: Int = 0,
    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Int? = null,
    val rating: Double = 0.0,
    val genres: List<String> = listOf(),
) {
    companion object {
        val EMPTY_SHOW = TvShow()
    }
}
