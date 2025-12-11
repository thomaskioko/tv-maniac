package com.thomaskioko.tvmaniac.util

import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

const val POSTER_PATH = "https://image.tmdb.org/t/p/original%s"

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class IosFormatterUtil : FormatterUtil {

    override fun formatTmdbPosterPath(imageUrl: String): String = POSTER_PATH.replace("%s", imageUrl)

    override fun formatDouble(number: Double?, scale: Int): Double {
        val formatter = NSNumberFormatter()
        formatter.minimumFractionDigits = scale.toULong()
        formatter.maximumFractionDigits = scale.toULong()
        formatter.roundingMode = 0u // NSNumberFormatterRoundUp
        formatter.numberStyle = 1u // Decimal
        return when {
            number != null -> formatter.stringFromNumber(NSNumber(number))?.toDouble() ?: 0.0
            else -> 0.0
        }
    }

    override fun formatDuration(number: Int): String {
        val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
        val numValue = number.toLong()
        val value = floor(log10(numValue.toDouble())).toInt()
        val base = value / 3

        val formatter = NSNumberFormatter()
        formatter.minimumFractionDigits = 0u
        formatter.maximumFractionDigits = 1u
        formatter.numberStyle = 1u // Decimal

        return when {
            value >= 3 && base < suffix.size -> {
                val scaledNum = numValue / 10.0.pow((base * 3).toDouble())
                "${formatter.stringFromNumber(NSNumber(scaledNum))}${suffix[base]}"
            }
            else -> {
                formatter.maximumFractionDigits = 0u
                formatter.stringFromNumber(NSNumber(integer = numValue)) ?: number.toString()
            }
        }
    }
}
