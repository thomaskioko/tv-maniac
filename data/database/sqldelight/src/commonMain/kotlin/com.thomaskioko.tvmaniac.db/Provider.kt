package com.thomaskioko.tvmaniac.db

/**
 * Content provider that an external id or per-provider catalog row belongs to. `MAL` and `ANIDB` are
 * defined ahead of use so anime support does not require an enum migration later.
 */
public enum class Provider {
    TRAKT,
    SIMKL,
    TMDB,
    TVDB,
    IMDB,
    MAL,
    ANIDB,
}
