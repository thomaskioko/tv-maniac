package com.thomaskioko.tvmaniac.domain.telemetry

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.CrashReportKeys
import com.thomaskioko.tvmaniac.core.logger.CrashReporter
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionManager
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionStatus
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private const val PROVIDER_TRAKT = "trakt"
private const val PROVIDER_SIMKL = "simkl"
private const val PROVIDER_NONE = "none"
private const val CONNECTIVITY_ONLINE = "online"
private const val CONNECTIVITY_OFFLINE = "offline"

@Inject
public class TelemetryContextInitializer(
    private val accountManager: AccountManager,
    private val subscriptionManager: SubscriptionManager,
    private val connectionChecker: InternetConnectionChecker,
    private val crashReporter: CrashReporter,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
) {

    public fun init() {
        accountManager.activeProvider
            .onEach { crashReporter.setCustomKey(CrashReportKeys.PROVIDER, it.toProviderKey()) }
            .launchIn(coroutineScope)

        accountManager.isConnected
            .onEach { crashReporter.setCustomKey(CrashReportKeys.SIGNED_IN, it.toString()) }
            .launchIn(coroutineScope)

        subscriptionManager.observeSubscriptionStatus()
            .onEach { crashReporter.setCustomKey(CrashReportKeys.PREMIUM, (it == SubscriptionStatus.Premium).toString()) }
            .launchIn(coroutineScope)

        connectionChecker.observeConnection()
            .onEach { crashReporter.setCustomKey(CrashReportKeys.CONNECTIVITY, if (it) CONNECTIVITY_ONLINE else CONNECTIVITY_OFFLINE) }
            .launchIn(coroutineScope)
    }
}

private fun SyncProviderSource?.toProviderKey(): String = when (this) {
    SyncProviderSource.TRAKT -> PROVIDER_TRAKT
    SyncProviderSource.SIMKL -> PROVIDER_SIMKL
    null -> PROVIDER_NONE
}
