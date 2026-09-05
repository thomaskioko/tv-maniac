package com.thomaskioko.tvmaniac.core.paging

import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.thomaskioko.tvmaniac.core.logger.CrashReportKeys
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class PaginatedRemoteMediatorTest {

    private val logger = FakeLogger()
    private val emptyState = PagingState<Int, Any>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = 20),
        leadingPlaceholderCount = 0,
    )

    @Test
    fun `should not report given fetch succeeds`() = runTest {
        val mediator = PaginatedRemoteMediator<Any>(logger, "PopularShows") {
            FetchResult.Success(endOfPaginationReached = true)
        }

        val result = mediator.load(LoadType.REFRESH, emptyState)

        result.shouldBeInstanceOf<RemoteMediator.MediatorResult.Success>()
        logger.recordedErrors.shouldBeEmpty()
    }

    @Test
    fun `should report an error with the source given fetch returns FetchResult Error`() = runTest {
        val cause = IllegalStateException("boom")
        val mediator = PaginatedRemoteMediator<Any>(logger, "PopularShows") { FetchResult.Error(cause) }

        val result = mediator.load(LoadType.REFRESH, emptyState)

        result.shouldBeInstanceOf<RemoteMediator.MediatorResult.Error>()
        val recorded = logger.recordedErrors.single()
        recorded.throwable shouldBe cause
        recorded.keys shouldBe mapOf(CrashReportKeys.SOURCE to "PopularShows")
    }

    @Test
    fun `should report an error with the source given fetch throws`() = runTest {
        val cause = IllegalStateException("boom")
        val mediator = PaginatedRemoteMediator<Any>(logger, "TrendingShows") { throw cause }

        val result = mediator.load(LoadType.REFRESH, emptyState)

        result.shouldBeInstanceOf<RemoteMediator.MediatorResult.Error>()
        val recorded = logger.recordedErrors.single()
        recorded.throwable shouldBe cause
        recorded.keys shouldBe mapOf(CrashReportKeys.SOURCE to "TrendingShows")
    }
}
