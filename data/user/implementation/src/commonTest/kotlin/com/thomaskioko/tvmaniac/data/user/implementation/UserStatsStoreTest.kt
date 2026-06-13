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
internal class UserStatsStoreTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var userStatsDao: DefaultUserStatsDao
    private lateinit var accountManager: FakeAccountManager
    private lateinit var requestManager: FakeRequestManagerRepository
    private lateinit var traktSource: FakeStatsSource
    private lateinit var store: UserStatsStore

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
        traktSource = FakeStatsSource(provider = AccountProvider.TRAKT)
        store = buildStore(
            sources = setOf(traktSource),
            accountManager = accountManager,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should persist stats given active provider returns non-null stats`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        traktSource.statsResponse = ApiResponse.Success(
            RemoteUserStats(
                showsWatched = 42L,
                episodesWatched = 500L,
                minutesWatched = 15000L,
            ),
        )

        store.stream(StoreReadRequest.fresh("test-user")).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        userStatsDao.observeUserProfileStats("test-user").test {
            val stats = awaitItem()
            stats.shouldNotBeNull()
            stats.showsWatched shouldBe 42
            stats.episodesWatched shouldBe 500
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should not write to dao given provider returns null stats`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        traktSource.statsResponse = ApiResponse.Success(null)

        store.stream(StoreReadRequest.fresh("test-user")).test {
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        userStatsDao.observeUserProfileStats("test-user").test {
            awaitItem().shouldBeNull()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should not write to dao given no active provider`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(null)

        store.stream(StoreReadRequest.fresh("test-user")).test {
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        userStatsDao.observeUserProfileStats("test-user").test {
            awaitItem().shouldBeNull()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should select simkl source given simkl provider is active`() = runTest(testDispatcher) {
        val simklSource = FakeStatsSource(provider = AccountProvider.SIMKL)
        simklSource.statsResponse = ApiResponse.Success(
            RemoteUserStats(
                showsWatched = 10L,
                episodesWatched = 100L,
                minutesWatched = 3000L,
            ),
        )
        val multiStore = buildStore(
            sources = setOf(traktSource, simklSource),
            accountManager = accountManager,
        )
        accountManager.setActiveProvider(AccountProvider.SIMKL)

        multiStore.stream(StoreReadRequest.fresh("test-user")).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        userStatsDao.observeUserProfileStats("test-user").test {
            val stats = awaitItem()
            stats.shouldNotBeNull()
            stats.showsWatched shouldBe 10
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should not write stats given simkl source returns null stats`() = runTest(testDispatcher) {
        val simklSource = FakeStatsSource(provider = AccountProvider.SIMKL)
        simklSource.statsResponse = ApiResponse.Success(null)
        val multiStore = buildStore(
            sources = setOf(traktSource, simklSource),
            accountManager = accountManager,
        )
        accountManager.setActiveProvider(AccountProvider.SIMKL)

        multiStore.stream(StoreReadRequest.fresh("test-user")).test {
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        userStatsDao.observeUserProfileStats("test-user").test {
            awaitItem().shouldBeNull()
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun buildStore(
        sources: Set<UserRemoteDataSource>,
        accountManager: FakeAccountManager,
    ): UserStatsStore = UserStatsStore(
        activeSource = { sources.firstOrNull { it.provider == accountManager.getActiveProvider() } },
        userStatsDao = userStatsDao,
        requestManagerRepository = requestManager,
        dispatchers = dispatchers,
    )
}

private class FakeStatsSource(
    override val provider: AccountProvider,
) : UserRemoteDataSource {
    var statsResponse: ApiResponse<RemoteUserStats?> = ApiResponse.Unauthenticated
    var profileResponse: ApiResponse<RemoteUserProfile> = ApiResponse.Unauthenticated

    override suspend fun getUserProfile(userId: String): ApiResponse<RemoteUserProfile> = profileResponse
    override suspend fun getUserStats(userId: String): ApiResponse<RemoteUserStats?> = statsResponse
}
