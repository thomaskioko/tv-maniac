package com.thomaskioko.tvmaniac.shared.core.util

import platform.Foundation.NSDateFormatter

const val POSTER_PATH = "https://image.tmdb.org/t/p/original"
const val DEFAULT_IMAGE_URL =
    "https://play-lh.googleusercontent.com/IO3niAyss5tFXAQP176P0Jk5rg_A_hfKPNqzC4gb15WjLPjo5I-f7oIZ9Dqxw2wPBAg"

actual object StringUtil {
    actual fun formatPosterPath(imageUrl: String?): String {
        return if (imageUrl.isNullOrBlank()) DEFAULT_IMAGE_URL
        else POSTER_PATH.plus(imageUrl)
    }

    actual fun formatDate(dateString: String?): String {
        val dateFormatter = NSDateFormatter().apply {
            this.dateFormat = "EEE, MMM d, yyyy"
        }
        return if (dateString != null) dateFormatter.dateFromString(dateString).toString() else "TBA"
    }
}
