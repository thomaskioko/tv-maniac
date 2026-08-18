package com.thomaskioko.tvmaniac.domain.accountswitcher

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AccountSwitchFailedException
import com.thomaskioko.tvmaniac.accountmanager.api.AuthManager
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.minutes

@Inject
public class ConnectAndSwitchProviderInteractor(
    private val authManagers: Map<SyncProviderSource, AuthManager>,
    private val accountManager: AccountManager,
    private val switchAccountInteractor: SwitchAccountInteractor,
) : Interactor<ConnectAndSwitchProviderInteractor.Params>() {

    public data class Params(val target: SyncProviderSource)

    override suspend fun doWork(params: Params) {
        authManagers[params.target]?.launchWebView()

        withTimeoutOrNull(OAUTH_TIMEOUT) {
            accountManager.accounts.first { accounts ->
                accounts.any { it.provider == params.target && it.isConnected }
            }
        } ?: throw AccountSwitchFailedException(params.target)

        switchAccountInteractor.executeSync(params.target)
    }

    private companion object {
        private val OAUTH_TIMEOUT = 2.minutes
    }
}
