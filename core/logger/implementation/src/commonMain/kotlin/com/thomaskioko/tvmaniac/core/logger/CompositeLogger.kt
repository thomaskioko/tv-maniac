package com.thomaskioko.tvmaniac.core.logger

import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

/**
 * A [Logger] implementation that delegates every call to all registered [Logger] instances.
 *
 * This allows multiple logging destinations (e.g., [KermitLogger] for local console output,
 * [FirebaseCrashLogger] for remote crash reporting) to be composed behind a single
 * [Logger] interface. Any class injecting [Logger] receives all destinations transparently.
 *
 * @param loggers The set of [Logger] implementations to delegate to.
 */
@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class CompositeLogger(
    private val loggers: Set<Logger>,
) : Logger {

    override fun setup(debugMode: Boolean) {
        loggers.forEach { it.setup(debugMode) }
    }

    override fun debug(message: String) {
        loggers.forEach { it.debug(message) }
    }

    override fun debug(tag: String, message: String) {
        loggers.forEach { it.debug(tag, message) }
    }

    override fun error(message: String, throwable: Throwable) {
        loggers.forEach { it.error(message, throwable) }
    }

    override fun error(tag: String, message: String) {
        loggers.forEach { it.error(tag, message) }
    }

    override fun info(message: String, throwable: Throwable) {
        loggers.forEach { it.info(message, throwable) }
    }

    override fun info(tag: String, message: String) {
        loggers.forEach { it.info(tag, message) }
    }

    override fun warning(message: String) {
        loggers.forEach { it.warning(message) }
    }

    override fun warning(tag: String, message: String) {
        loggers.forEach { it.warning(tag, message) }
    }

    override fun verbose(message: String) {
        loggers.forEach { it.verbose(message) }
    }

    override fun verbose(tag: String, message: String) {
        loggers.forEach { it.verbose(tag, message) }
    }

    override fun recordException(throwable: Throwable, tag: String) {
        loggers.forEach { it.recordException(throwable, tag) }
    }

    override fun setUserId(userId: String) {
        loggers.forEach { it.setUserId(userId) }
    }

    override fun setCustomKey(key: String, value: String) {
        loggers.forEach { it.setCustomKey(key, value) }
    }
}
