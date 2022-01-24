package com.thomaskioko.tvmaniac.shared.core.util

expect object StringUtil {
    fun formatPosterPath(imageUrl: String?): String
    fun formatDate(dateString: String?): String
}
