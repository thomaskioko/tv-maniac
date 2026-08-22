package com.thomaskioko.tvmaniac.domain.accountswitcher

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.ConnectedAccount
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.data.logout.api.LogoutHandler
import com.thomaskioko.tvmaniac.data.logout.testing.FakeLogoutHandler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SwitchAccountInteractorTest {

    private val accountManager = FakeAccountManager()
    private val logoutHandler = FakeLogoutHandler()

    private var resyncProfileRan = false
    private var resyncLibraryRan = false
    private var resyncContinueWatchingRan = false

    private fun buildInteractor(scope: CoroutineScope): SwitchAccountInteractor = SwitchAccountInteractor(
        logoutHandler = logoutHandler,
        accountManager = accountManager,
        resyncProfile = ResyncProfile { resyncProfileRan = true },
        resyncLibrary = ResyncLibrary { resyncLibraryRan = true },
        resyncContinueWatching = ResyncContinueWatching { resyncContinueWatchingRan = true },
        appScopeLauncher = FakeAppScopeLauncher(scope),
    )

    @Test
    fun `should clear state and set the new provider given the user switches accounts`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)

        buildInteractor(backgroundScope).executeSync(SyncProviderSource.SIMKL)

        logoutHandler.accountAndTrackingDataCleared shouldBe true
        logoutHandler.accountDataCleared shouldBe false
        accountManager.getActiveProvider() shouldBe SyncProviderSource.SIMKL
    }

    @Test
    fun `should run every resync interactor given the user switches accounts`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)

        buildInteractor(backgroundScope).executeSync(SyncProviderSource.SIMKL)
        testScheduler.runCurrent()

        resyncProfileRan shouldBe true
        resyncLibraryRan shouldBe true
        resyncContinueWatchingRan shouldBe true
    }

    @Test
    fun `should log out of the old provider before clearing given one is connected`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        accountManager.setAccounts(
            listOf(ConnectedAccount(provider = SyncProviderSource.TRAKT, isConnected = true, isActive = true)),
        )

        buildInteractor(backgroundScope).executeSync(SyncProviderSource.SIMKL)

        accountManager.lastLogoutProvider shouldBe SyncProviderSource.TRAKT
    }

    @Test
    fun `should not log out given no provider is connected on first activation`() = runTest {
        accountManager.setActiveProvider(null)

        buildInteractor(backgroundScope).executeSync(SyncProviderSource.SIMKL)

        accountManager.lastLogoutProvider shouldBe null
        accountManager.getActiveProvider() shouldBe SyncProviderSource.SIMKL
    }

    @Test
    fun `should log out then clear then activate then resync given the user switches accounts`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        accountManager.setAccounts(
            listOf(ConnectedAccount(provider = SyncProviderSource.TRAKT, isConnected = true, isActive = true)),
        )
        val events = mutableListOf<String>()

        val interactor = SwitchAccountInteractor(
            logoutHandler = object : LogoutHandler {
                override suspend fun clearAccountData() {
                    events.add("clearAccountData")
                }

                override suspend fun clearAccountAndTrackingData() {
                    events.add("clearAccountAndTrackingData")
                }
            },
            accountManager = object : AccountManager by accountManager {
                override fun getActiveProvider(): SyncProviderSource? = accountManager.getActiveProvider()
                override suspend fun logout(provider: SyncProviderSource) {
                    events.add("logout:${provider.name}")
                    accountManager.logout(provider)
                }
                override suspend fun setActive(provider: SyncProviderSource) {
                    events.add("setActive:${provider.name}")
                    accountManager.setActive(provider)
                }
            },
            resyncProfile = { events.add("resyncProfile") },
            resyncLibrary = { events.add("resyncLibrary") },
            resyncContinueWatching = { events.add("resyncContinueWatching") },
            appScopeLauncher = FakeAppScopeLauncher(backgroundScope),
        )

        interactor.executeSync(SyncProviderSource.SIMKL)
        testScheduler.runCurrent()

        events shouldBe listOf(
            "logout:TRAKT",
            "clearAccountAndTrackingData",
            "setActive:SIMKL",
            "resyncProfile",
            "resyncLibrary",
            "resyncContinueWatching",
        )
    }
}
