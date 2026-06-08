package com.thomaskioko.tvmaniac.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp

/**
 * Kind of image surface. Bundles the size-class-keyed [width] with the matching [aspect] so a call
 * site declares the kind once instead of pairing a [Layout] width with an [ImageDimens] aspect by
 * hand. Layout sizing only; image fetching/bucket selection stays with the TMDB interceptor.
 */
public enum class ImageType {
    Poster,
    Backdrop,
    Cast,
    ;

    /** Aspect ratio for the kind, expressed as width / height (matching `Modifier.aspectRatio`). */
    public val aspect: Float
        get() = when (this) {
            Poster -> ImageDimens.PosterAspect
            Backdrop -> ImageDimens.BackdropAspect
            Cast -> ImageDimens.CastAspect
        }

    /** Width for the kind at the current window width size class. */
    public val width: Dp
        @Composable @ReadOnlyComposable
        get() = when (this) {
            Poster -> Layout.posterWidth
            Backdrop -> Layout.backdropCardWidth
            Cast -> Layout.castCardWidth
        }
}
