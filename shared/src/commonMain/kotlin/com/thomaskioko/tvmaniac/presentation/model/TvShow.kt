package com.thomaskioko.tvmaniac.presentation.model

data class TvShow(
    val id: Int = 0,
    val title: String = "",
    val overview: String = "",
    val language: String = "",
    val posterImageUrl: String = "",
    val backdropImageUrl: String = "",
    val year: String = "",
    val status: String = "",
    val votes: Int = 0,
    val averageVotes: Double = 0.0,
    val isInWatchlist: Boolean = false,
    val genreIds: List<Int> = listOf(),
) {
    companion object {
        val EMPTY_SHOW = TvShow()
    }
}
