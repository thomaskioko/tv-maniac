package com.thomaskioko.tvmaniac.datasource.repository.shows

import com.thomaskioko.tvmaniac.MockData.getShow
import com.thomaskioko.tvmaniac.datasource.cache.category.CategoryCache
import com.thomaskioko.tvmaniac.datasource.cache.show_category.ShowCategoryCache
import com.thomaskioko.tvmaniac.datasource.cache.shows.TvShowCache
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepositoryImpl
import com.thomaskioko.tvmaniac.util.runBlockingTest
import com.thomaskioko.tvmaniac.util.testCoroutineDispatcher
import com.thomaskioko.tvmaniac.util.testCoroutineScope
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
