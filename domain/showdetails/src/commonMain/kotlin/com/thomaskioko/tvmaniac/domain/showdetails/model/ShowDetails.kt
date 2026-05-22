package com.thomaskioko.tvmaniac.domain.showdetails.model

public data class ShowDetails(
    val tmdbId: Long,
    val traktId: Long,
    val title: String,
    val overview: String,
    val language: String?,
    val posterImageUrl: String?,
    val backdropImageUrl: String?,
    val year: String,
    val status: String?,
    val votes: Long = 0,
    val rating: Double,
    val isInLibrary: Boolean,
    val genres: List<String> = emptyList(),
) {
    public companion object {
        public val Empty: ShowDetails = ShowDetails(
            tmdbId = 0,
            traktId = 0,
            title = "",
            overview = "",
            language = null,
            posterImageUrl = null,
            backdropImageUrl = null,
            year = "",
            status = null,
            votes = 0,
            rating = 0.0,
            isInLibrary = false,
            genres = emptyList(),
        )
    }
}

public data class Casts(
    val id: Long,
    val name: String,
    val profileUrl: String? = null,
    val characterName: String,
)

public data class Providers(
    val id: Long,
    val logoUrl: String?,
    val name: String,
)

public data class Season(
    val seasonId: Long,
    val tvShowId: Long,
    val name: String,
    val seasonNumber: Long,
    val watchedCount: Int = 0,
    val totalCount: Int = 0,
) {
    val progressPercentage: Float
        get() = if (totalCount > 0) watchedCount.toFloat() / totalCount else 0f
}

public data class Show(
    val traktId: Long,
    val title: String,
    val posterImageUrl: String?,
    val backdropImageUrl: String?,
    val isInLibrary: Boolean,
)

public data class Trailer(
    val showTmdbId: Long,
    val key: String,
    val name: String,
    val youtubeThumbnailUrl: String,
)
