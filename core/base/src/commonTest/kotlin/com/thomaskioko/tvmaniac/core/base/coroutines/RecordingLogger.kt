package com.thomaskioko.tvmaniac.core.base.coroutines

import com.thomaskioko.tvmaniac.core.logger.Logger

internal class RecordingLogger : Logger {

    private val recorded = mutableListOf<LoggedError>()

    val errors: List<LoggedError> get() = recorded.toList()

    override fun error(message: String, throwable: Throwable) {
        recorded += LoggedError(tag = null, message = message, throwable = throwable)
    }

    override fun error(tag: String, message: String) {
        recorded += LoggedError(tag = tag, message = message, throwable = null)
    }

    override fun error(tag: String, message: String, throwable: Throwable) {
        recorded += LoggedError(tag = tag, message = message, throwable = throwable)
    }
}

