package com.thomaskioko.tvmaniac.core.logger

import co.touchlab.crashkios.crashlytics.CrashlyticsKotlin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class CrashKiosCrashlytics : Crashlytics {
    override fun setCustomValue(key: String, value: String): Unit = CrashlyticsKotlin.setCustomValue(key, value)
    override fun sendHandledException(throwable: Throwable): Unit = CrashlyticsKotlin.sendHandledException(throwable)
    override fun setUserId(userId: String): Unit = CrashlyticsKotlin.setUserId(userId)
    override fun logMessage(message: String): Unit = CrashlyticsKotlin.logMessage(message)
}
