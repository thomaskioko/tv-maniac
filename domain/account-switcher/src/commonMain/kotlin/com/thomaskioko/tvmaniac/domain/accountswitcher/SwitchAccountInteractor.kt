package com.thomaskioko.tvmaniac.domain.accountswitcher

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.base.coroutines.AppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.logout.api.LogoutHandler
import dev.zacsweers.metro.Inject

@Inject
public class SwitchAccountInteractor(
    private val logoutHandler: LogoutHandler,
    private val accountManager: AccountManager,
    private val resyncProfile: ResyncProfile,
    private val resyncLibrary: ResyncLibrary,
    private val resyncContinueWatching: ResyncContinueWatching,
    private val appScopeLauncher: AppScopeLauncher,
) : Interactor<SyncProviderSource>() {

    override suspend fun doWork(params: SyncProviderSource) {
        val old = accountManager.getActiveProvider()
        old?.let { accountManager.logout(it) }
        logoutHandler.clear()
        accountManager.setActive(params)
        appScopeLauncher.launch(RESYNC_TAG) {
            resyncProfile()
            resyncLibrary()
            resyncContinueWatching()
        }
    }

    private companion object {
        private const val RESYNC_TAG = "SwitchAccountResync"
    }
}
