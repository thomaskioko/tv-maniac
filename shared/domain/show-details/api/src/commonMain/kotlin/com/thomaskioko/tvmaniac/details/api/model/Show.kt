package com.thomaskioko.tvmaniac.details.api.model

data class Show(
    val traktId: Long = 0,
    val tmdbId: Long? = 0,
    val title: String = "",
    val overview: String = "",
    val language: String? = null,
    val posterImageUrl: String? = null,
    val backdropImageUrl: String? = null,
    val year: String = "",
    val status: String? = null,
    val votes: Long = 0,
    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Long? = null,
    val rating: Double = 0.0,
    val genres: List<String> = listOf(),
    val isFollowed: Boolean = false,
) {
    companion object {
        val EMPTY_SHOW = Show()
    }
}
