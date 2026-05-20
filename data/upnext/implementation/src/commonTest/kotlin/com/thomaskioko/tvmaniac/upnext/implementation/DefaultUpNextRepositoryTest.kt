package com.thomaskioko.tvmaniac.upnext.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeNextEpisodeDao
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsDao
import io.kotest.matchers.shouldBe
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
    private val datastoreRepository = FakeDatastoreRepository()
    private val nextEpisodeDao = FakeNextEpisodeDao()
    private val followedShowsDao = FakeFollowedShowsDao()

    private lateinit var repository: DefaultUpNextRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = DefaultUpNextRepository(
            nextEpisodeDao = nextEpisodeDao,
            datastoreRepository = datastoreRepository,
            followedShowsDao = followedShowsDao,
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

    private fun followedShow(
        traktId: Long,
        pendingAction: PendingAction = PendingAction.NOTHING,
    ): FollowedShowEntry = FollowedShowEntry(
        traktId = traktId,
        tmdbId = traktId,
        followedAt = Instant.fromEpochMilliseconds(NOW),
        pendingAction = pendingAction,
    )

    private companion object {
        private const val NOW = 1_750_000_000_000L
    }
}
