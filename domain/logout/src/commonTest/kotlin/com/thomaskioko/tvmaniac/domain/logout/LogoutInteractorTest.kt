package com.thomaskioko.tvmaniac.domain.logout

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class LogoutInteractorTest {
    private lateinit var traktAuthRepository: FakeTraktAuthRepository
    private lateinit var userRepository: FakeUserRepository
    private lateinit var datastoreRepository: FakeDatastoreRepository
    private lateinit var logoutInteractor: LogoutInteractor

    @BeforeTest
    fun setup() {
        traktAuthRepository = FakeTraktAuthRepository()
        userRepository = FakeUserRepository()
        datastoreRepository = FakeDatastoreRepository()
        logoutInteractor = LogoutInteractor(
            traktAuthRepository = traktAuthRepository,
            userRepository = userRepository,
            datastoreRepository = datastoreRepository,
        )
    }

    @Test
    fun `should successfully logout and emit success status`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)

        logoutInteractor(Unit).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }

    @Test
    fun `should complete logout successfully`() = runTest {
        logoutInteractor(Unit).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }
}
