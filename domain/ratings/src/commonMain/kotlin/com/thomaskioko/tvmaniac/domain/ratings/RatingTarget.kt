package com.thomaskioko.tvmaniac.domain.ratings

public sealed interface RatingTarget {
    public data class Show(
        val title: String,
        val year: String?,
        val posterUrl: String?,
    ) : RatingTarget

    public data class Season(
        val title: String,
        val showName: String,
        val posterUrl: String?,
    ) : RatingTarget

    public data class Episode(
        val title: String,
        val showName: String,
        val seasonNumber: Long,
        val episodeNumber: Long,
        val backdropUrl: String?,
    ) : RatingTarget
}
