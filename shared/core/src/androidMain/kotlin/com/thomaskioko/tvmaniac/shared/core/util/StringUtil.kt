package com.thomaskioko.tvmaniac.shared.core.util

import co.touchlab.kermit.Logger
import java.text.SimpleDateFormat
import java.util.Locale

const val POSTER_PATH = "https://image.tmdb.org/t/p/original%s"
const val DEFAULT_IMAGE_URL =
    "https://play-lh.googleusercontent.com/IO3niAyss5tFXAQP176P0Jk5rg_A_hfKPNqzC4gb15WjLPjo5I-f7oIZ9Dqxw2wPBAg"

actual object StringUtil {
    actual fun formatPosterPath(imageUrl: String?): String {
        return if (imageUrl.isNullOrBlank()) DEFAULT_IMAGE_URL else String.format(
            POSTER_PATH,
            imageUrl
        )
    }

    actual fun formatDate(dateString: String?): String {
        var result = "TBA"
        try {
            if (dateString != null) {
                val date = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())
                    .parse(dateString)
                result = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
                    .format(date)
            }
        } catch (exception: Exception) {
            Logger.e("formatDate::  $dateString ${exception.message}", exception)
        }
        return result
    }
}
