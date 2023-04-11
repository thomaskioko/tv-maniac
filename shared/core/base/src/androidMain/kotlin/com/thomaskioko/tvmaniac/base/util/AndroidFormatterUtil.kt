package com.thomaskioko.tvmaniac.base.util

import me.tatarka.inject.annotations.Inject
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

const val POSTER_PATH = "https://image.tmdb.org/t/p/original%s"
const val DEFAULT_IMAGE_URL =
    "https://play-lh.googleusercontent.com/IO3niAyss5tFXAQP176P0Jk5rg_A_hfKPNqzC4gb15WjLPjo5I-f7oIZ9Dqxw2wPBAg"

@Inject
class AndroidFormatterUtil : FormatterUtil {

    override fun formatTmdbPosterPath(imageUrl: String?): String {
        return if (imageUrl.isNullOrBlank()) DEFAULT_IMAGE_URL
        else String.format(POSTER_PATH, imageUrl)
    }

    override fun formatDouble(number: Double?, scale: Int): Double {
        return number?.toBigDecimal()?.setScale(scale, RoundingMode.UP)?.toDouble() ?: 0.0
    }

    override fun formatDuration(number: Int): String {
        val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
        val numValue = number.toLong()
        val value = floor(log10(numValue.toDouble())).toInt()
        val base = value / 3
        return when {
            value >= 3 && base < suffix.size -> {
                DecimalFormat("#0.0")
                    .format(numValue / 10.0.pow((base * 3).toDouble())) + suffix[base]
            }

            else -> {
                DecimalFormat("#,##0").format(numValue)
            }
        }
    }
}
