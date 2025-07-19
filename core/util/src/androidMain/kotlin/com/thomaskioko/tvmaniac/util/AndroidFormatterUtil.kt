package com.thomaskioko.tvmaniac.util

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

const val POSTER_PATH = "https://image.tmdb.org/t/p/original%s"

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AndroidFormatterUtil : FormatterUtil {

    override fun formatTmdbPosterPath(imageUrl: String): String = String.format(POSTER_PATH, imageUrl)

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
                DecimalFormat("#0.0").format(numValue / 10.0.pow((base * 3).toDouble())) + suffix[base]
            }
            else -> {
                DecimalFormat("#,##0").format(numValue)
            }
        }
    }
}
