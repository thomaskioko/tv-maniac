package com.thomaskioko.tvmaniac.datasource.repository

import com.thomaskioko.tvmaniac.MockData.getShow
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.core.test.testCoroutineDispatcher
import com.thomaskioko.tvmaniac.core.test.testCoroutineScope
import com.thomaskioko.tvmaniac.discover.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.discover.implementation.repository.TvShowsRepositoryImpl
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlin.test.AfterTest
import kotlin.test.Ignore
import kotlin.test.Test

internal class TvShowRepositoryTest {

    private var apiService = mockk<TvShowsService>()
    private var tvShowCache = spyk<TvShowCache>()
    private var showCategoryCache = spyk<ShowCategoryCache>()
    private var categoryCache = spyk<CategoryCache>()

    private val repository: TvShowsRepositoryImpl = TvShowsRepositoryImpl(
        apiService,
        tvShowCache,
        categoryCache,
        showCategoryCache,
        testCoroutineScope,
        testCoroutineDispatcher
    )

    @AfterTest
    fun tearDownAll() {
        unmockkAll()
    }

    @Ignore
    @Test
    fun givenDataIsCached_thenGetCachedTvShowIsLoadedFromCache() = runBlockingTest {
        every { tvShowCache.getTvShow(any()) } returns flowOf(getShow())

        repository.observeShow(84958)

        coVerify(exactly = 0) {
            apiService.getTvShowDetails(any())
        }

        coVerify { tvShowCache.getTvShow(84958) }
    }

    @Test
    fun givenDataIsCached_thenWatchlistIsFetched() = runBlockingTest {
        repository.observeWatchlist()

        coVerify { tvShowCache.getWatchlist() }
    }
}