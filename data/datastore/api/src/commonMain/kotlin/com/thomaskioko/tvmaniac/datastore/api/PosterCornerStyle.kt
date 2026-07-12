package com.thomaskioko.tvmaniac.datastore.api

/**
 * Poster corner preset. [cornerRadius] is the corner radius in density-independent units, shared by
 * both platforms; [SHARP] is the neutral square-cornered baseline.
 */
public enum class PosterCornerStyle(public val cornerRadius: Float) {
    SHARP(0f),
    CLASSIC(4f),
    ROUNDED(8f),
    PILL(16f),
}
