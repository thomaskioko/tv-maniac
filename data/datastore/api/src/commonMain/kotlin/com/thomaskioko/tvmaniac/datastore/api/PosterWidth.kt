package com.thomaskioko.tvmaniac.datastore.api

/**
 * Poster width preset. [scale] multiplies the responsive base width and composes with the window
 * size class; [STANDARD] is the neutral 1.0 baseline.
 */
public enum class PosterWidth(public val scale: Float) {
    COMPACT(0.85f),
    STANDARD(1.0f),
    COMFORTABLE(1.15f),
    LARGE(1.25f),
}
