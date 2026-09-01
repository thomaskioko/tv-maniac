package com.thomaskioko.tvmaniac.ui.widget

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

internal suspend fun Context.loadPoster(
    url: String?,
    widthPx: Int = ROW_POSTER_WIDTH_PX,
    heightPx: Int = ROW_POSTER_HEIGHT_PX,
): Bitmap? {
    if (url.isNullOrBlank()) return null

    val request = ImageRequest.Builder(this)
        .data(url)
        .size(widthPx, heightPx)
        .allowHardware(false)
        .build()

    return (imageLoader.execute(request) as? SuccessResult)?.drawable?.toBitmap()
}

private const val ROW_POSTER_WIDTH_PX = 185
private const val ROW_POSTER_HEIGHT_PX = 278

internal const val TILE_POSTER_WIDTH_PX: Int = 500
internal const val TILE_POSTER_HEIGHT_PX: Int = 750
