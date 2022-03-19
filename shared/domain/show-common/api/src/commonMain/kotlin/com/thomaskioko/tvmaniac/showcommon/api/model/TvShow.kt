package com.thomaskioko.tvmaniac.showcommon.api.model

data class TvShow(
    val id: Long = 0,
    val title: String = "",
    val overview: String = "",
    val language: String = "",
    val posterImageUrl: String = "",
    val backdropImageUrl: String = "",
    val year: String = "",
    val status: String? = null,
    val votes: Int = 0,
    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Int? = null,
    val averageVotes: Double = 0.0,
    val following: Boolean = false,
    val genreIds: List<Int> = listOf(),
) {
    companion object {
        val EMPTY_SHOW = TvShow()
    }
}
