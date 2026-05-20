package com.thomaskioko.tvmaniac.upnext.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingDao
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeNextEpisodeDao
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsDao
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultUpNextRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val requestManagerRepository = FakeRequestManagerRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val datastoreRepository = FakeDatastoreRepository()
    private val nextEpisodeDao = FakeNextEpisodeDao()
    private val followedShowsDao = FakeFollowedShowsDao()
    private val continueWatchingDao = FakeContinueWatchingDao()
    private val syncObserver = FakeSyncObserver()

    private lateinit var repository: DefaultUpNextRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = DefaultUpNextRepository(
            nextEpisodeDao = nextEpisodeDao,
            datastoreRepository = datastoreRepository,
            followedShowsDao = followedShowsDao,
            continueWatchingDao = continueWatchingDao,
            showDetailsRepository = showDetailsRepository,
            seasonDetailsRepository = seasonDetailsRepository,
            watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
            requestManagerRepository = requestManagerRepository,
            syncObserver = syncObserver,
            logger = FakeLogger(),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should exclude shows pending delete from followed count`() = runTest {
        followedShowsDao.upsert(followedShow(traktId = 1L, pendingAction = PendingAction.UPLOAD))
        followedShowsDao.upsert(followedShow(traktId = 2L, pendingAction = PendingAction.NOTHING))
        followedShowsDao.upsert(followedShow(traktId = 3L, pendingAction = PendingAction.DELETE))

        repository.observeFollowedShowsCount().test {
            awaitItem() shouldBe 2
        }
    }

    @Test
    fun `should sync season metadata and watched episodes for each followed show given sync invalid`() = runTest {
        followedShowsDao.upsert(followedShow(traktId = 1L))
        followedShowsDao.upsert(followedShow(traktId = 2L))
        requestManagerRepository.requestValid = false

        repository.fetchUpNextEpisodes(forceRefresh = false)

        seasonDetailsRepository.getSyncedShowIds() shouldContainExactlyInAnyOrder listOf(1L, 2L)
        watchedEpisodeSyncRepository.getSyncedShowIds() shouldContainExactlyInAnyOrder listOf(1L, 2L)
        requestManagerRepository.upsertCalled shouldBe true
    }

    @Test
    fun `should skip metadata sync when request still valid and no force refresh`() = runTest {
        followedShowsDao.upsert(followedShow(traktId = 1L))
        requestManagerRepository.requestValid = true

        repository.fetchUpNextEpisodes(forceRefresh = false)

        seasonDetailsRepository.getSyncedShowIds().size shouldBe 0
        watchedEpisodeSyncRepository.getSyncedShowIds().size shouldBe 0
        requestManagerRepository.upsertCalled shouldBe false
    }

    @Test
    fun `should force metadata sync when force refresh given valid request`() = runTest {
        followedShowsDao.upsert(followedShow(traktId = 1L))
        requestManagerRepository.requestValid = true

        repository.fetchUpNextEpisodes(forceRefresh = true)

        seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(1L)
        watchedEpisodeSyncRepository.getSyncedShowIds() shouldBe listOf(1L)
        watchedEpisodeSyncRepository.wasForceRefreshUsed() shouldBe true
        requestManagerRepository.upsertCalled shouldBe true
    }

    @Test
    fun `should no-op when no followed or watched shows exist`() = runTest {
        requestManagerRepository.requestValid = false

        repository.fetchUpNextEpisodes(forceRefresh = false)

        seasonDetailsRepository.getSyncedShowIds().size shouldBe 0
        watchedEpisodeSyncRepository.getSyncedShowIds().size shouldBe 0
        requestManagerRepository.upsertCalled shouldBe false
    }

    @Test
    fun `should sync each followed id once given followed-only set`() = runTest {
        followedShowsDao.upsert(followedShow(traktId = 1L))
        followedShowsDao.upsert(followedShow(traktId = 2L))
        requestManagerRepository.requestValid = false

        repository.fetchUpNextEpisodes(forceRefresh = false)

        showDetailsRepository.fetchInvocations().map { it.id } shouldContainExactlyInAnyOrder listOf(1L, 2L)
        seasonDetailsRepository.getSyncedShowIds() shouldContainExactlyInAnyOrder listOf(1L, 2L)
        watchedEpisodeSyncRepository.getSyncedShowIds() shouldContainExactlyInAnyOrder listOf(1L, 2L)
    }

    @Test
    fun `should sync each watched id once given watched-only set`() = runTest {
        continueWatchingDao.upsert(watchedShow(traktId = 1L))
        continueWatchingDao.upsert(watchedShow(traktId = 2L))
        requestManagerRepository.requestValid = false

        repository.fetchUpNextEpisodes(forceRefresh = false)

        showDetailsRepository.fetchInvocations().map { it.id } shouldContainExactlyInAnyOrder listOf(1L, 2L)
        seasonDetailsRepository.getSyncedShowIds() shouldContainExactlyInAnyOrder listOf(1L, 2L)
        watchedEpisodeSyncRepository.getSyncedShowIds() shouldContainExactlyInAnyOrder listOf(1L, 2L)
    }

    @Test
    fun `should sync each distinct id once given overlapping followed and watched sets`() = runTest {
        followedShowsDao.upsert(followedShow(traktId = 1L))
        followedShowsDao.upsert(followedShow(traktId = 2L))
        continueWatchingDao.upsert(watchedShow(traktId = 2L))
        continueWatchingDao.upsert(watchedShow(traktId = 3L))
        requestManagerRepository.requestValid = false

        repository.fetchUpNextEpisodes(forceRefresh = false)

        showDetailsRepository.fetchInvocations().map { it.id } shouldContainExactlyInAnyOrder listOf(1L, 2L, 3L)
        seasonDetailsRepository.getSyncedShowIds() shouldContainExactlyInAnyOrder listOf(1L, 2L, 3L)
        watchedEpisodeSyncRepository.getSyncedShowIds() shouldContainExactlyInAnyOrder listOf(1L, 2L, 3L)
    }

    @Test
    fun `should propagate force refresh to all repositories for every id`() = runTest {
        followedShowsDao.upsert(followedShow(traktId = 1L))
        continueWatchingDao.upsert(watchedShow(traktId = 2L))
        requestManagerRepository.requestValid = true

        repository.fetchUpNextEpisodes(forceRefresh = true)

        showDetailsRepository.fetchInvocations().all { it.forceRefresh } shouldBe true
        showDetailsRepository.fetchInvocations().size shouldBe 2
        watchedEpisodeSyncRepository.wasForceRefreshUsed() shouldBe true
    }

    @Test
    fun `should short-circuit empty union before fetch and request-manager upsert`() = runTest {
        requestManagerRepository.requestValid = false

        repository.fetchUpNextEpisodes(forceRefresh = false)

        showDetailsRepository.fetchInvocations().size shouldBe 0
        seasonDetailsRepository.getSyncedShowIds().size shouldBe 0
        watchedEpisodeSyncRepository.getSyncedShowIds().size shouldBe 0
        requestManagerRepository.upsertCalled shouldBe false
    }

    @Test
    fun `should log SyncError to observer when per-show fetch fails`() = runTest {
        followedShowsDao.upsert(followedShow(traktId = 1L))
        requestManagerRepository.requestValid = false
        showDetailsRepository.setFetchError(RuntimeException("rate-limited 429"))

        syncObserver.errors.test {
            repository.fetchUpNextEpisodes(forceRefresh = false)

            val event = awaitItem()
            event.shouldBeInstanceOf<SyncError.BackgroundSyncFailed>()
            event.cause.message shouldBe "rate-limited 429"
        }
    }

    @Test
    fun `should exclude pending-delete followed shows from fetch union`() = runTest {
        followedShowsDao.upsert(followedShow(traktId = 1L, pendingAction = PendingAction.NOTHING))
        followedShowsDao.upsert(followedShow(traktId = 2L, pendingAction = PendingAction.DELETE))
        requestManagerRepository.requestValid = false

        repository.fetchUpNextEpisodes(forceRefresh = false)

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(1L)
        seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(1L)
        watchedEpisodeSyncRepository.getSyncedShowIds() shouldBe listOf(1L)
    }

    private fun followedShow(
        traktId: Long,
        pendingAction: PendingAction = PendingAction.NOTHING,
    ): FollowedShowEntry = FollowedShowEntry(
        traktId = traktId,
        tmdbId = traktId,
        followedAt = Instant.fromEpochMilliseconds(NOW),
        pendingAction = pendingAction,
    )

    private fun watchedShow(traktId: Long): ContinueWatchingEntry = ContinueWatchingEntry(
        traktId = traktId,
        tmdbId = traktId,
        airedEpisodes = 0L,
        completedCount = 1L,
        lastWatchedAt = NOW - 10_000L,
        lastUpdatedAt = NOW - 10_000L,
    )

    private companion object {
        private const val NOW = 1_750_000_000_000L
    }
}
