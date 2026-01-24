package com.thomaskioko.tvmaniac.util

import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

public const val POSTER_PATH: String = "https://image.tmdb.org/t/p/original%s"

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidFormatterUtil : FormatterUtil {

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

    override fun formatDateTime(epochMillis: Long, pattern: String): String {
        val date = Date(epochMillis)
        val formatter = SimpleDateFormat(pattern, Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(date)
    }
}
