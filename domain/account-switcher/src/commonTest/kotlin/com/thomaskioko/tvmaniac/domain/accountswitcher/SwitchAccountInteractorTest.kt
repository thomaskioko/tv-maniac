package com.thomaskioko.tvmaniac.domain.accountswitcher

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.interactor.executeSync
import com.thomaskioko.tvmaniac.data.logout.api.LogoutHandler
import com.thomaskioko.tvmaniac.data.logout.testing.FakeLogoutHandler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SwitchAccountInteractorTest {

    private val accountManager = FakeAccountManager()
    private val logoutHandler = FakeLogoutHandler()

    private var resyncLibraryRan = false
    private var resyncContinueWatchingRan = false

    private fun buildInteractor(scope: CoroutineScope): SwitchAccountInteractor = SwitchAccountInteractor(
        logoutHandler = logoutHandler,
        accountManager = accountManager,
        resyncLibrary = ResyncLibrary { resyncLibraryRan = true },
        resyncContinueWatching = ResyncContinueWatching { resyncContinueWatchingRan = true },
        appScopeLauncher = FakeAppScopeLauncher(scope),
    )

    @Test
    fun `should clear state and set new provider given switch`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)

        buildInteractor(backgroundScope).executeSync(AccountProvider.SIMKL)

        logoutHandler.cleared shouldBe true
        accountManager.getActiveProvider() shouldBe AccountProvider.SIMKL
    }

    @Test
    fun `should run both resync interactors given switch`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)

        buildInteractor(backgroundScope).executeSync(AccountProvider.SIMKL)
        testScheduler.runCurrent()

        resyncLibraryRan shouldBe true
        resyncContinueWatchingRan shouldBe true
    }

    @Test
    fun `should logout old provider before clear given active provider exists`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)

        buildInteractor(backgroundScope).executeSync(AccountProvider.SIMKL)

        accountManager.lastLogoutProvider shouldBe AccountProvider.TRAKT
    }

    @Test
    fun `should not logout given no active provider on first activation`() = runTest {
        accountManager.setActiveProvider(null)

        buildInteractor(backgroundScope).executeSync(AccountProvider.SIMKL)

        accountManager.lastLogoutProvider shouldBe null
        accountManager.getActiveProvider() shouldBe AccountProvider.SIMKL
    }

    @Test
    fun `should execute in order logout then clear then setActive then resync given switch`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        val events = mutableListOf<String>()

        val interactor = SwitchAccountInteractor(
            logoutHandler = object : LogoutHandler {
                override suspend fun clear() {
                    events.add("clear")
                }
            },
            accountManager = object : AccountManager by accountManager {
                override fun getActiveProvider(): AccountProvider? = accountManager.getActiveProvider()
                override suspend fun logout(provider: AccountProvider) {
                    events.add("logout:${provider.name}")
                    accountManager.logout(provider)
                }
                override suspend fun setActive(provider: AccountProvider) {
                    events.add("setActive:${provider.name}")
                    accountManager.setActive(provider)
                }
            },
            resyncLibrary = ResyncLibrary { events.add("resyncLibrary") },
            resyncContinueWatching = ResyncContinueWatching { events.add("resyncContinueWatching") },
            appScopeLauncher = FakeAppScopeLauncher(backgroundScope),
        )

        interactor.executeSync(AccountProvider.SIMKL)
        testScheduler.runCurrent()

        events shouldBe listOf("logout:TRAKT", "clear", "setActive:SIMKL", "resyncLibrary", "resyncContinueWatching")
    }
}
