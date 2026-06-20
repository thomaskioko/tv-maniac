package com.thomaskioko.tvmaniac.domain.logout

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.logout.testing.FakeLogoutHandler
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.data.user.testing.createTestProfile
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class LogoutInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val fakeAccountManager = FakeAccountManager()
    private val fakeUserRepository = FakeUserRepository(createTestProfile())
    private val fakeDatastoreRepository = FakeDatastoreRepository()
    private val fakeLogoutHandler = FakeLogoutHandler()

    private val interactor = LogoutInteractor(
        accountManager = fakeAccountManager,
        userRepository = fakeUserRepository,
        datastoreRepository = fakeDatastoreRepository,
        logoutHandler = fakeLogoutHandler,
    )

    @Test
    fun `should logout active provider given active provider is set`() = runTest(testDispatcher) {
        fakeAccountManager.setActiveProvider(AccountProvider.TRAKT)

        interactor.executeSync(Unit)

        fakeAccountManager.lastLogoutProvider shouldBe AccountProvider.TRAKT
    }

    @Test
    fun `should delegate clearing to provider state cleaner given logout executed`() = runTest(testDispatcher) {
        interactor.executeSync(Unit)

        fakeLogoutHandler.cleared shouldBe true
    }

    @Test
    fun `should not logout given no active provider`() = runTest(testDispatcher) {
        fakeAccountManager.setActiveProvider(null)

        interactor.executeSync(Unit)

        fakeAccountManager.lastLogoutProvider.shouldBeNull()
    }
}
