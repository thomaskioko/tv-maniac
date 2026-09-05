package com.thomaskioko.tvmaniac.core.logger

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlinx.atomicfu.atomic

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class FirebaseCrashLogger(
    private val crashReporter: CrashReporter,
) : Logger {

    private val reportedFailures = atomic<Set<String>>(emptySet())

    override fun error(message: String, throwable: Throwable) {
        crashReporter.recordException(throwable)
    }

    override fun error(tag: String, message: String) {
        crashReporter.log("[$tag] $message")
    }

    override fun error(tag: String, message: String, throwable: Throwable, keys: Map<String, String>) {
        val reportKeys = keys + (CrashReportKeys.TAG to tag)
        if (markReported(dedupeKey(throwable, reportKeys))) {
            crashReporter.log("[$tag] $message")
            crashReporter.recordException(throwable, reportKeys)
        } else {
            crashReporter.log(breadcrumb(tag, message, throwable, reportKeys + (CrashReportKeys.REPEAT to "true")))
        }
    }

    override fun warning(tag: String, message: String, throwable: Throwable, keys: Map<String, String>) {
        crashReporter.log(breadcrumb(tag, message, throwable, keys))
    }

    override fun recordException(throwable: Throwable, keys: Map<String, String>) {
        crashReporter.recordException(throwable, keys)
    }

    override fun setUserId(userId: String) {
        crashReporter.setUserId(userId)
    }

    override fun setCustomKey(key: String, value: String) {
        crashReporter.setCustomKey(key, value)
    }

    private fun markReported(key: String): Boolean {
        while (true) {
            val current = reportedFailures.value
            if (key in current) return false
            if (reportedFailures.compareAndSet(current, current + key)) return true
        }
    }

    private fun dedupeKey(throwable: Throwable, keys: Map<String, String>): String =
        listOf(
            throwable::class.qualifiedName.orEmpty(),
            keys[CrashReportKeys.TAG].orEmpty(),
            keys[CrashReportKeys.ENDPOINT].orEmpty(),
            keys[CrashReportKeys.STATUS].orEmpty(),
        ).joinToString(separator = "|")

    private fun breadcrumb(tag: String, message: String, throwable: Throwable, keys: Map<String, String>): String {
        val repeatSuffix = keys[CrashReportKeys.REPEAT]?.let { ": ${CrashReportKeys.REPEAT}" }.orEmpty()
        return "[$tag] $message: ${throwable::class.simpleName}: ${throwable.message}$repeatSuffix"
    }
}
