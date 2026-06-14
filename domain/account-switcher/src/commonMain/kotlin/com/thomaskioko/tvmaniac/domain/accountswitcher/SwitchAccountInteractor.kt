package com.thomaskioko.tvmaniac.domain.accountswitcher

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.logout.api.LogoutHandler
import dev.zacsweers.metro.Inject

@Inject
public class SwitchAccountInteractor(
    private val logoutHandler: LogoutHandler,
    private val accountManager: AccountManager,
    private val resyncLibrary: ResyncLibrary,
    private val resyncContinueWatching: ResyncContinueWatching,
) : Interactor<AccountProvider>() {

    override suspend fun doWork(params: AccountProvider) {
        logoutHandler.clear()
        accountManager.setActive(params)
        resyncLibrary()
        resyncContinueWatching()
    }
}
