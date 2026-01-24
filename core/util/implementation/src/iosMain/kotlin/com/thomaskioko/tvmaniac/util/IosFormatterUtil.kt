package com.thomaskioko.tvmaniac.util

import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.localeWithLocaleIdentifier
import platform.Foundation.timeZoneWithName
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

public const val POSTER_PATH: String = "https://image.tmdb.org/t/p/original%s"

@Inject
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

    override fun formatDateTime(epochMillis: Long, pattern: String): String {
        val date = NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0)
        val formatter = NSDateFormatter()
        formatter.locale = NSLocale.localeWithLocaleIdentifier("en_US_POSIX")
        formatter.dateFormat = pattern
        NSTimeZone.timeZoneWithName("UTC")?.let { formatter.timeZone = it }
        return formatter.stringFromDate(date)
    }
}
