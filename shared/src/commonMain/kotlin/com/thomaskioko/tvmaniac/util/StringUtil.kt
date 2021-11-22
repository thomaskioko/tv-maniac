package com.thomaskioko.tvmaniac.util

internal expect object StringUtil {
    fun formatPosterPath(imageUrl: String?): String
}
