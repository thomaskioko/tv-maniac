package com.thomaskioko.tvmaniac.core.logger

import co.touchlab.kermit.Severity
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import co.touchlab.kermit.Logger as KermitLogger

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, multibinding = true)
public class KermitLogger : Logger {

    override fun setup(debugMode: Boolean) {
        KermitLogger.setMinSeverity(if (debugMode) Severity.Debug else Severity.Error)
    }

    override fun debug(message: String) {
        KermitLogger.d(message)
    }

    override fun debug(tag: String, message: String) {
        KermitLogger.withTag(tag).d(message)
    }

    override fun error(message: String, throwable: Throwable) {
        KermitLogger.e(message, throwable)
    }

    override fun error(tag: String, message: String) {
        KermitLogger.withTag(tag).e(message)
    }

    override fun info(message: String, throwable: Throwable) {
        KermitLogger.i(message, throwable)
    }

    override fun info(tag: String, message: String) {
        KermitLogger.withTag(tag).i(message)
    }

    override fun warning(message: String) {
        KermitLogger.w(message)
    }

    override fun warning(tag: String, message: String) {
        KermitLogger.withTag(tag).w(message)
    }

    override fun verbose(message: String) {
        KermitLogger.v(message)
    }

    override fun verbose(tag: String, message: String) {
        KermitLogger.withTag(tag).v(message)
    }
}
