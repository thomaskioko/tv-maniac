package com.thomaskioko.tvmaniac.domain.ratings

public sealed interface RatingTarget {
    public data class Show(
        val title: String,
        val year: String?,
    ) : RatingTarget

    public data class Season(
        val title: String,
        val showName: String,
    ) : RatingTarget

    public data class Episode(
        val title: String,
        val showName: String,
        val seasonNumber: Long,
        val episodeNumber: Long,
    ) : RatingTarget
}
