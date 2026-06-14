package com.thomaskioko.tvmaniac.domain.accountswitcher

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.interactor.executeSync
import com.thomaskioko.tvmaniac.data.logout.api.LogoutHandler
import com.thomaskioko.tvmaniac.data.logout.testing.FakeLogoutHandler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SwitchAccountInteractorTest {

    private val accountManager = FakeAccountManager()
    private val logoutHandler = FakeLogoutHandler()

    private var resyncLibraryRan = false
    private var resyncContinueWatchingRan = false

    private fun buildInteractor(): SwitchAccountInteractor = SwitchAccountInteractor(
        logoutHandler = logoutHandler,
        accountManager = accountManager,
        resyncLibrary = ResyncLibrary { resyncLibraryRan = true },
        resyncContinueWatching = ResyncContinueWatching { resyncContinueWatchingRan = true },
    )

    @Test
    fun `should clear state and set new provider given switch`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)

        buildInteractor().executeSync(AccountProvider.SIMKL)

        logoutHandler.cleared shouldBe true
        accountManager.getActiveProvider() shouldBe AccountProvider.SIMKL
    }

    @Test
    fun `should run both resync interactors given switch`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)

        buildInteractor().executeSync(AccountProvider.SIMKL)

        resyncLibraryRan shouldBe true
        resyncContinueWatchingRan shouldBe true
    }

    @Test
    fun `should clear before setting new provider given switch`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        val events = mutableListOf<String>()

        val interactor = SwitchAccountInteractor(
            logoutHandler = object : LogoutHandler {
                override suspend fun clear() {
                    events.add("clear")
                }
            },
            accountManager = object : AccountManager by accountManager {
                override suspend fun setActive(provider: AccountProvider) {
                    events.add("setActive:${provider.name}")
                    accountManager.setActive(provider)
                }
            },
            resyncLibrary = ResyncLibrary { events.add("resyncLibrary") },
            resyncContinueWatching = ResyncContinueWatching { events.add("resyncContinueWatching") },
        )

        interactor.executeSync(AccountProvider.SIMKL)

        events shouldBe listOf("clear", "setActive:SIMKL", "resyncLibrary", "resyncContinueWatching")
    }
}
