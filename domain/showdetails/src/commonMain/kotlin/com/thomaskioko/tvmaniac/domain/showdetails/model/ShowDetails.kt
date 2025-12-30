package com.thomaskioko.tvmaniac.domain.showdetails.model

public data class ShowDetails(
    val tmdbId: Long,
    val title: String,
    val overview: String,
    val language: String?,
    val posterImageUrl: String?,
    val backdropImageUrl: String?,
    val year: String,
    val status: String?,
    val votes: Long = 0,
    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Long? = null,
    val rating: Double,
    val isInLibrary: Boolean,
    val hasWebViewInstalled: Boolean,
    val genres: List<String>,
    val providers: List<Providers>,
    val castsList: List<Casts>,
    val seasonsList: List<Season>,
    val recommendedShows: List<Show>,
    val similarShows: List<Show>,
    val trailersList: List<Trailer>,
)

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
    val tmdbId: Long,
    val title: String,
    val posterImageUrl: String?,
    val backdropImageUrl: String?,
    val isInLibrary: Boolean,
)

public data class Trailer(
    val showId: Long,
    val key: String,
    val name: String,
    val youtubeThumbnailUrl: String,
)
