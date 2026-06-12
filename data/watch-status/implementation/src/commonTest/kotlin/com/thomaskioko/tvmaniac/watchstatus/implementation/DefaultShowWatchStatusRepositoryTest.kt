package com.thomaskioko.tvmaniac.watchstatus.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchProgress
import com.thomaskioko.tvmaniac.watchstatus.testing.FakeShowWatchStatusDao
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DefaultShowWatchStatusRepositoryTest {

    private val dao = FakeShowWatchStatusDao()
    private val resolver = object : ShowIdResolver {
        override fun showIdForTmdbId(tmdbId: Long): Id<ShowId> = Id(tmdbId)
        override fun showIdForTraktId(traktId: Long): Id<ShowId> = Id(traktId)
    }
    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val repository = DefaultShowWatchStatusRepository(
        dao = dao,
        showIdResolver = resolver,
        dateTimeProvider = FakeDateTimeProvider(),
        dispatchers = dispatchers,
    )

    @Test
    fun `should write COMPLETED when all aired episodes are watched`() = runTest(testDispatcher) {
        dao.setWatchProgress(Id(1388L), ShowWatchProgress(watchedCount = 24, totalCount = 24))

        repository.refresh(1388L)

        dao.getStatus(Id(1388L)) shouldBe WatchStatus.COMPLETED
    }

    @Test
    fun `should write WATCHING when some episodes are watched`() = runTest(testDispatcher) {
        dao.setWatchProgress(Id(1388L), ShowWatchProgress(watchedCount = 5, totalCount = 24))

        repository.refresh(1388L)

        dao.getStatus(Id(1388L)) shouldBe WatchStatus.WATCHING
    }

    @Test
    fun `should write WATCHLIST when no episodes are watched`() = runTest(testDispatcher) {
        dao.setWatchProgress(Id(1388L), ShowWatchProgress(watchedCount = 0, totalCount = 24))

        repository.refresh(1388L)

        dao.getStatus(Id(1388L)) shouldBe WatchStatus.WATCHLIST
    }

    @Test
    fun `should not write a status when the show has no progress row`() = runTest(testDispatcher) {
        repository.refresh(404L)

        dao.getStatus(Id(404L)).shouldBeNull()
    }
}
