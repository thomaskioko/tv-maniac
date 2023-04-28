package com.thomaskioko.tvmaniac.util

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.util.model.Configs
import me.tatarka.inject.annotations.Inject

@Inject
class KermitLogger(
    private val configs: Configs,
) {

    /**
     * Log a debug message
     */
    fun debug(message: String) {
        if (configs.isDebug) {
            Logger.d(message)
        }
    }

    /**
     * Log a debug message with tag
     */
    fun debug(tag: String, message: String) {
        if (configs.isDebug) {
            Logger.withTag(tag).d(message)
        }
    }

    /**
     * Log an error message
     */
    fun error(message: String, throwable: Throwable) {
        if (configs.isDebug) {
            Logger.e(message, throwable)
        }
    }

    /**
     * Log an error message with tag
     */
    fun error(tag: String, message: String) {
        if (configs.isDebug) {
            Logger.withTag(tag).e(message)
        }
    }

    /**
     * Log an error message
     */
    fun info(message: String, throwable: Throwable) {
        if (configs.isDebug) {
            Logger.i(message, throwable)
        }
    }

    /**
     * Log an error message with tag
     */
    fun info(tag: String, message: String) {
        if (configs.isDebug) {
            Logger.withTag(tag).i(message)
        }
    }

    /**
     * Log a warning message
     */
    fun warning(message: String) {
        if (configs.isDebug) {
            Logger.w(message)
        }
    }

    /**
     * Log a warning message with tag
     */
    fun warning(tag: String, message: String) {
        if (configs.isDebug) {
            Logger.withTag(tag).w(message)
        }
    }

    /**
     * Log a verbose message
     */
    fun verbose(message: String) {
        if (configs.isDebug) {
            Logger.w(message)
        }
    }

    /**
     * Log a verbose message with tag
     */
    fun verbose(tag: String, message: String) {
        if (configs.isDebug) {
            Logger.withTag(tag).v(message)
        }
    }
}
