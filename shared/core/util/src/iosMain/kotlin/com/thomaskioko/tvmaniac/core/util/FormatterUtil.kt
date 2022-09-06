package com.thomaskioko.tvmaniac.core.util

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter

const val POSTER_PATH = "https://image.tmdb.org/t/p/original"
const val DEFAULT_IMAGE_URL =
    "https://play-lh.googleusercontent.com/IO3niAyss5tFXAQP176P0Jk5rg_A_hfKPNqzC4gb15WjLPjo5I-f7oIZ9Dqxw2wPBAg"

actual object FormatterUtil {
    actual fun formatPosterPath(imageUrl: String?): String {
        return if (imageUrl.isNullOrBlank()) DEFAULT_IMAGE_URL else POSTER_PATH.plus(imageUrl)
    }

    actual fun formatDouble(number: Double?, scale: Int): Double {
        val formatter = NSNumberFormatter()
        formatter.minimumFractionDigits = 0u
        formatter.maximumFractionDigits = 1u
        formatter.numberStyle = 1u //Decimal
        return when {
            number != null -> formatter.stringFromNumber(NSNumber(number))?.toDouble() ?: 0.0
            else -> 0.0
        }
    }

}
