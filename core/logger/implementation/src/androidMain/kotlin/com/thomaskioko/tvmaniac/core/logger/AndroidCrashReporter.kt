package com.thomaskioko.tvmaniac.core.logger

import com.google.firebase.crashlytics.FirebaseCrashlytics
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidCrashReporter(
    private val crashlytics: FirebaseCrashlytics?,
) : CrashReporter {

    override fun setCollectionEnabled(enabled: Boolean) {
        crashlytics?.isCrashlyticsCollectionEnabled = enabled
    }

    override fun recordException(throwable: Throwable) {
        crashlytics?.recordException(throwable)
    }

    override fun recordException(throwable: Throwable, tag: String) {
        crashlytics?.setCustomKey("tag", tag)
        crashlytics?.recordException(throwable)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics?.setCustomKey(key, value)
    }

    override fun setUserId(userId: String) {
        crashlytics?.setUserId(userId)
    }

    override fun log(message: String) {
        crashlytics?.log(message)
    }
}
