package com.thomaskioko.tvmaniac.util

const val POSTER_PATH = "https://image.tmdb.org/t/p/original%s"
const val DEFAULT_IMAGE_URL =
    "https://play-lh.googleusercontent.com/IO3niAyss5tFXAQP176P0Jk5rg_A_hfKPNqzC4gb15WjLPjo5I-f7oIZ9Dqxw2wPBAg"

internal actual object StringUtil {
    actual fun formatPosterPath(imageUrl: String?): String {
        return if (imageUrl.isNullOrBlank()) DEFAULT_IMAGE_URL else String.format(POSTER_PATH, imageUrl)
    }
}
