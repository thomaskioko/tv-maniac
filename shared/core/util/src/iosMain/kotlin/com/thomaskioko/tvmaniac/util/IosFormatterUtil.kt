package com.thomaskioko.tvmaniac.util

import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import kotlin.math.abs
import kotlin.math.sign

const val POSTER_PATH = "https://image.tmdb.org/t/p/original"
const val DEFAULT_IMAGE_URL =
    "https://play-lh.googleusercontent.com/IO3niAyss5tFXAQP176P0Jk5rg_A_hfKPNqzC4gb15WjLPjo5I-f7oIZ9Dqxw2wPBAg"

@Inject
class IosFormatterUtil : FormatterUtil {

    override fun formatTmdbPosterPath(imageUrl: String?): String {
        return if (imageUrl.isNullOrBlank()) DEFAULT_IMAGE_URL else POSTER_PATH.plus(imageUrl)
    }

    override fun formatDouble(number: Double?, scale: Int): Double {
        val formatter = NSNumberFormatter()
        formatter.minimumFractionDigits = 0u
        formatter.maximumFractionDigits = 1u
        formatter.numberStyle = 1u //Decimal
        return when {
            number != null -> formatter.stringFromNumber(NSNumber(number))?.toDouble() ?: 0.0
            else -> 0.0
        }
    }

    override fun formatDuration(number: Int): String {
        val formatter = NSNumberFormatter()
        formatter.minimumFractionDigits = 0u
        formatter.maximumFractionDigits = 1u
        formatter.numberStyle = 1u //Decimal

        val num = NSNumber((abs(number) / 1000))

        return if (abs(number) > 999) "${(sign(number.toDouble()) * num.doubleValue) / 10.0} k"
        else (sign(number.toDouble()) * abs(number)).toString()
    }

}
