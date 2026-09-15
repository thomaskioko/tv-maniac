package com.thomaskioko.tvmaniac.search.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.search.api.model.RemoteSearchShow
import com.thomaskioko.tvmaniac.search.testing.FakeSearchRemoteDataSource
import com.thomaskioko.tvmaniac.shows.implementation.DefaultTvShowsDao
import com.thomaskioko.tvmaniac.tmdb.testing.FakeTmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import dev.zacsweers.metro.providerOf
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultSearchRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val remoteSource = FakeSearchRemoteDataSource(provider = SyncProviderSource.TRAKT)
    private val accountManager = FakeAccountManager()
    private val requestManager = FakeRequestManagerRepository(initialRequestValid = true)
    private val dateTimeProvider = FakeDateTimeProvider()

    private lateinit var searchDao: DefaultSearchDao
    private lateinit var repository: DefaultSearchRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val tvShowsDao = DefaultTvShowsDao(database = database, dispatchers = dispatchers)
        searchDao = DefaultSearchDao(database = database, showIdResolver = showIdResolver, dispatchers = dispatchers)
        val store = SearchShowStore(
            searchDao = searchDao,
            tvShowsDao = tvShowsDao,
            activeSearchRemoteDataSource = providerOf(remoteSource),
            tmdbDetailsDataSource = FakeTmdbShowDetailsNetworkDataSource(),
            formatterUtil = FakeFormatterUtil(),
            dateTimeProvider = dateTimeProvider,
            requestManagerRepository = requestManager,
            databaseTransactionRunner = RepositoryTestTransactionRunner,
            dispatchers = dispatchers,
        )
        repository = DefaultSearchRepository(
            searchDao = searchDao,
            store = store,
            accountManager = accountManager,
            requestManagerRepository = requestManager,
            dateTimeProvider = dateTimeProvider,
        )
        remoteSource.setSearchResult("query", listOf(buildRemoteShow()))
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should fetch given the request is expired`() = runTest(testDispatcher) {
        requestManager.requestExpired = true

        repository.search(query = "query", forceRefresh = false)

        remoteSource.searchCalls shouldHaveSize 1
    }

    @Test
    fun `should not fetch given the request is valid`() = runTest(testDispatcher) {
        requestManager.requestExpired = false

        repository.search(query = "query", forceRefresh = false)

        remoteSource.searchCalls.shouldBeEmpty()
    }

    @Test
    fun `should fetch given forceRefresh is true even when the request is valid`() = runTest(testDispatcher) {
        requestManager.requestExpired = false

        repository.search(query = "query", forceRefresh = true)

        remoteSource.searchCalls shouldHaveSize 1
    }

    @Test
    fun `should store results under different keys given the active provider differs`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        repository.search(query = "Query", forceRefresh = true)

        accountManager.setActiveProvider(SyncProviderSource.SIMKL)
        repository.search(query = "Query", forceRefresh = true)

        searchDao.observeResults("trakt:query").first() shouldHaveSize 1
        searchDao.observeResults("simkl:query").first() shouldHaveSize 1
    }

    @Test
    fun `should return recent searches newest first`() = runTest(testDispatcher) {
        dateTimeProvider.setCurrentTimeMillis(1_000L)
        repository.saveRecentSearch("first")
        dateTimeProvider.setCurrentTimeMillis(2_000L)
        repository.saveRecentSearch("second")

        repository.observeRecentSearches().first() shouldBe listOf("second", "first")
    }

    @Test
    fun `should dedupe recent searches case insensitively`() = runTest(testDispatcher) {
        repository.saveRecentSearch("Breaking Bad")
        repository.saveRecentSearch("breaking bad")

        repository.observeRecentSearches().first() shouldHaveSize 1
    }

    @Test
    fun `should limit recent searches to ten`() = runTest(testDispatcher) {
        repeat(12) { index ->
            dateTimeProvider.setCurrentTimeMillis(index.toLong())
            repository.saveRecentSearch("query$index")
        }

        repository.observeRecentSearches().first() shouldHaveSize 10
    }

    @Test
    fun `should remove a recent search`() = runTest(testDispatcher) {
        repository.saveRecentSearch("query")

        repository.removeRecentSearch("query")

        repository.observeRecentSearches().first().shouldBeEmpty()
    }

    @Test
    fun `should clear recent searches`() = runTest(testDispatcher) {
        repository.saveRecentSearch("one")
        repository.saveRecentSearch("two")

        repository.clearRecentSearches()

        repository.observeRecentSearches().first().shouldBeEmpty()
    }

    private fun buildRemoteShow(): RemoteSearchShow = RemoteSearchShow(
        providerShowId = "1",
        tmdbId = 100L,
        title = "Show",
        year = 2008,
        overview = "Overview",
        status = "Ended",
        episodeCount = 62,
        rating = 9.0,
        votes = 1000L,
        genres = listOf("drama"),
        language = "en",
        score = 100.0,
    )
}

private object RepositoryTestTransactionRunner : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T = block()
}
