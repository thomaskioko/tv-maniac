package com.thomaskioko.tvmaniac.util

const val POSTER_PATH = "https://image.tmdb.org/t/p/original%s"

internal actual object StringUtil {
    actual fun formatPosterPath(imageUrl: String): String {
        return String.format(POSTER_PATH, imageUrl)
    }
}