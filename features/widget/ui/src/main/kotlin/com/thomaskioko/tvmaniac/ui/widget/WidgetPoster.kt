package com.thomaskioko.tvmaniac.ui.widget

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

internal suspend fun Context.loadPoster(url: String?): Bitmap? {
    if (url.isNullOrBlank()) return null

    val request = ImageRequest.Builder(this)
        .data(url)
        .size(POSTER_WIDTH_PX, POSTER_HEIGHT_PX)
        .allowHardware(false)
        .build()

    return (imageLoader.execute(request) as? SuccessResult)?.drawable?.toBitmap()
}

private const val POSTER_WIDTH_PX = 185
private const val POSTER_HEIGHT_PX = 278
