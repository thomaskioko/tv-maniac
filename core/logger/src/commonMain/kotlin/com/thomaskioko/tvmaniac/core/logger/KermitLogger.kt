package com.thomaskioko.tvmaniac.core.logger

import co.touchlab.kermit.Logger
import me.tatarka.inject.annotations.Inject

@Inject
class KermitLogger(
  private val isDebug: Boolean,
) {

  /** Log a debug message */
  fun debug(message: String) {
    if (isDebug) {
      Logger.d(message)
    }
  }

  /** Log a debug message with tag */
  fun debug(tag: String, message: String) {
    if (isDebug) {
      Logger.withTag(tag).d(message)
    }
  }

  /** Log an error message */
  fun error(message: String, throwable: Throwable) {
    if (isDebug) {
      Logger.e(message, throwable)
    }
  }

  /** Log an error message with tag */
  fun error(tag: String, message: String) {
    if (isDebug) {
      Logger.withTag(tag).e(message)
    }
  }

  /** Log an error message */
  fun info(message: String, throwable: Throwable) {
    if (isDebug) {
      Logger.i(message, throwable)
    }
  }

  /** Log an error message with tag */
  fun info(tag: String, message: String) {
    if (isDebug) {
      Logger.withTag(tag).i(message)
    }
  }

  /** Log a warning message */
  fun warning(message: String) {
    if (isDebug) {
      Logger.w(message)
    }
  }

  /** Log a warning message with tag */
  fun warning(tag: String, message: String) {
    if (isDebug) {
      Logger.withTag(tag).w(message)
    }
  }

  /** Log a verbose message */
  fun verbose(message: String) {
    if (isDebug) {
      Logger.w(message)
    }
  }

  /** Log a verbose message with tag */
  fun verbose(tag: String, message: String) {
    if (isDebug) {
      Logger.withTag(tag).v(message)
    }
  }
}
