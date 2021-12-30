package com.thomaskioko.tvmaniac.datasource.repository.shows

import com.thomaskioko.tvmaniac.MockData.getShow
import com.thomaskioko.tvmaniac.MockData.makeShowList
import com.thomaskioko.tvmaniac.datasource.cache.category.CategoryCache
import com.thomaskioko.tvmaniac.datasource.cache.show_category.ShowCategoryCache
import com.thomaskioko.tvmaniac.datasource.cache.shows.TvShowCache
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepositoryImpl
import com.thomaskioko.tvmaniac.util.runBlocking
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class TvShowRepositoryTest {

    @RelaxedMockK
    lateinit var apiService: TvShowsService

    @RelaxedMockK
    lateinit var tvShowCache: TvShowCache

    @RelaxedMockK
    lateinit var showCategoryCache: ShowCategoryCache

    @RelaxedMockK
    lateinit var categoryCache: CategoryCache

    private lateinit var repository: TvShowsRepositoryImpl

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this)
        repository = TvShowsRepositoryImpl(
            apiService,
            tvShowCache,
            categoryCache,
            showCategoryCache,
            mockk()
        )
    }

    @AfterTest
    fun tearDownAll() {
        unmockkAll()
    }

    @Test
    fun givenDataIsCached_thenGetCachedTvShowIsLoadedFromCache() = runBlocking {
        every { tvShowCache.getTvShow(84958) } returns flowOf(getShow())

        repository.observeShow(84958)

        coVerify(exactly = 0) {
            apiService.getTvShowDetails(1)
        }

        verify { tvShowCache.getTvShow(84958) }
    }

    @Test
    fun givenDataIsCached_thenWatchlistIsFetched() = runBlocking {
        every { tvShowCache.getTvShows() } returns flowOf(makeShowList())

        repository.observeWatchlist()

        coVerify { tvShowCache.getWatchlist() }
    }
}
