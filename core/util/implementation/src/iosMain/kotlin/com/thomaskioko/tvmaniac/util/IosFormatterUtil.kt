package com.thomaskioko.tvmaniac.util

import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.localeWithLocaleIdentifier
import platform.Foundation.timeZoneWithName
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

public const val POSTER_PATH: String = "https://image.tmdb.org/t/p/original%s"
private const val COMPACT_NUMBER_THRESHOLD = 10_000L

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class IosFormatterUtil : FormatterUtil {

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
        // TODO:: Use Localization
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

    override fun formatCompactNumber(number: Long): String {
        val formatter = NSNumberFormatter()
        formatter.numberStyle = 1u // Decimal

        if (number < COMPACT_NUMBER_THRESHOLD) {
            formatter.maximumFractionDigits = 0u
            return formatter.stringFromNumber(NSNumber(integer = number)) ?: number.toString()
        }

        val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T', 'P', 'E')
        val magnitude = floor(log10(number.toDouble())).toInt()
        val base = (magnitude / 3).coerceAtMost(suffix.size - 1)
        val scaled = number / 10.0.pow((base * 3).toDouble())

        formatter.minimumFractionDigits = 0u
        formatter.maximumFractionDigits = 1u
        val scaledString = formatter.stringFromNumber(NSNumber(scaled)) ?: scaled.toString()
        return "$scaledString${suffix[base]}"
    }

    override fun formatDateTime(epochMillis: Long, pattern: String): String {
        val date = NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0)
        val formatter = NSDateFormatter()
        formatter.locale = NSLocale.localeWithLocaleIdentifier("en_US_POSIX")
        formatter.dateFormat = pattern
        NSTimeZone.timeZoneWithName("UTC")?.let { formatter.timeZone = it }
        return formatter.stringFromDate(date)
    }
}
