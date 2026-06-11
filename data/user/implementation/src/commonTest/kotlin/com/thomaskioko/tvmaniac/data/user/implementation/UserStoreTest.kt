package com.thomaskioko.tvmaniac.data.user.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.user.api.UserRemoteDataSource
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserProfile
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserStats
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mobilenativefoundation.store.store5.StoreReadRequest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class UserStoreTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var userDao: DefaultUserDao
    private lateinit var userStatsDao: DefaultUserStatsDao
    private lateinit var accountManager: FakeAccountManager
    private lateinit var requestManager: FakeRequestManagerRepository
    private lateinit var traktSource: FakeUserSource
    private lateinit var store: UserStore

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        accountManager = FakeAccountManager()
        requestManager = FakeRequestManagerRepository(initialRequestValid = false)
        userStatsDao = DefaultUserStatsDao(
            database = database,
            formatterUtil = FakeFormatterUtil(),
            dispatchers = dispatchers,
        )
        userDao = DefaultUserDao(
            database = database,
            userStatsDao = userStatsDao,
            dispatchers = dispatchers,
        )
        traktSource = FakeUserSource(provider = AccountProvider.TRAKT)
        store = buildStore(setOf(traktSource))
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should persist profile given active provider returns success`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        traktSource.profileResponse = ApiResponse.Success(
            RemoteUserProfile(
                slug = "john-doe",
                username = "johndoe",
                fullName = "John Doe",
                avatarUrl = "https://example.com/avatar.jpg",
                backgroundUrl = null,
            ),
        )

        store.stream(StoreReadRequest.fresh("me")).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        userDao.observeCurrentUser().test {
            val profile = awaitItem()
            profile.shouldNotBeNull()
            profile.slug shouldBe "john-doe"
            profile.username shouldBe "johndoe"
            profile.fullName shouldBe "John Doe"
            profile.avatarUrl shouldBe "https://example.com/avatar.jpg"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should not write to dao given no active provider`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(null)

        store.stream(StoreReadRequest.fresh("me")).test {
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        userDao.observeCurrentUser().test {
            awaitItem().shouldBeNull()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should select simkl source given simkl provider is active`() = runTest(testDispatcher) {
        val simklSource = FakeUserSource(provider = AccountProvider.SIMKL)
        simklSource.profileResponse = ApiResponse.Success(
            RemoteUserProfile(
                slug = "simkl-user",
                username = "simkluser",
                fullName = null,
                avatarUrl = null,
                backgroundUrl = null,
            ),
        )
        val multiStore = buildStore(setOf(traktSource, simklSource))
        accountManager.setActiveProvider(AccountProvider.SIMKL)

        multiStore.stream(StoreReadRequest.fresh("me")).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        userDao.observeCurrentUser().test {
            val profile = awaitItem()
            profile.shouldNotBeNull()
            profile.slug shouldBe "simkl-user"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should not write trakt data given simkl provider is active`() = runTest(testDispatcher) {
        val simklSource = FakeUserSource(provider = AccountProvider.SIMKL)
        simklSource.profileResponse = ApiResponse.Success(
            RemoteUserProfile(
                slug = "simkl-user",
                username = "simkluser",
                fullName = null,
                avatarUrl = null,
                backgroundUrl = null,
            ),
        )
        traktSource.profileResponse = ApiResponse.Success(
            RemoteUserProfile(
                slug = "trakt-user",
                username = "traktuser",
                fullName = null,
                avatarUrl = null,
                backgroundUrl = null,
            ),
        )
        val multiStore = buildStore(setOf(traktSource, simklSource))
        accountManager.setActiveProvider(AccountProvider.SIMKL)

        multiStore.stream(StoreReadRequest.fresh("me")).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        userDao.observeCurrentUser().test {
            val profile = awaitItem()
            profile.shouldNotBeNull()
            profile.slug shouldBe "simkl-user"
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun buildStore(sources: Set<UserRemoteDataSource>): UserStore = UserStore(
        sources = sources,
        accountManager = accountManager,
        userDao = userDao,
        requestManagerRepository = requestManager,
        dispatchers = dispatchers,
    )
}

private class FakeUserSource(
    override val provider: AccountProvider,
) : UserRemoteDataSource {
    var profileResponse: ApiResponse<RemoteUserProfile> = ApiResponse.Unauthenticated
    var statsResponse: ApiResponse<RemoteUserStats?> = ApiResponse.Unauthenticated

    override suspend fun getUserProfile(userId: String): ApiResponse<RemoteUserProfile> = profileResponse
    override suspend fun getUserStats(userId: String): ApiResponse<RemoteUserStats?> = statsResponse
}
