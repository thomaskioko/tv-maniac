package com.thomaskioko.tvmaniac.domain.accountswitcher

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.base.coroutines.AppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.logout.api.LogoutHandler
import dev.zacsweers.metro.Inject

@Inject
public class SwitchAccountInteractor(
    private val logoutHandler: LogoutHandler,
    private val accountManager: AccountManager,
    private val resyncLibrary: ResyncLibrary,
    private val resyncContinueWatching: ResyncContinueWatching,
    private val appScopeLauncher: AppScopeLauncher,
) : Interactor<AccountProvider>() {

    override suspend fun doWork(params: AccountProvider) {
        val old = accountManager.getActiveProvider()
        old?.let { accountManager.logout(it) }
        logoutHandler.clear()
        accountManager.setActive(params)
        appScopeLauncher.launch(RESYNC_TAG) {
            resyncLibrary()
            resyncContinueWatching()
        }
    }

    private companion object {
        private const val RESYNC_TAG = "SwitchAccountResync"
    }
}
