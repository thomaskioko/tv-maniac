package com.thomaskioko.tvmaniac.util

import kotlin.math.abs
import kotlin.math.sign
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter

const val POSTER_PATH = "https://image.tmdb.org/t/p/original"

@Inject
class IosFormatterUtil : FormatterUtil {

  override fun formatTmdbPosterPath(imageUrl: String): String {
    return POSTER_PATH.plus(imageUrl)
  }

  override fun formatDouble(number: Double?, scale: Int): Double {
    val formatter = NSNumberFormatter()
    formatter.minimumFractionDigits = 0u
    formatter.maximumFractionDigits = 1u
    formatter.numberStyle = 1u // Decimal
    return when {
      number != null -> formatter.stringFromNumber(NSNumber(number))?.toDouble() ?: 0.0
      else -> 0.0
    }
  }

  override fun formatDuration(number: Int): String {
    val formatter = NSNumberFormatter()
    formatter.minimumFractionDigits = 0u
    formatter.maximumFractionDigits = 1u
    formatter.numberStyle = 1u // Decimal

    val num = NSNumber((abs(number) / 1000))

    return if (abs(number) > 999) {
      "${(sign(number.toDouble()) * num.doubleValue) / 10.0} k"
    } else {
      (sign(number.toDouble()) * abs(number)).toString()
    }
  }
}
