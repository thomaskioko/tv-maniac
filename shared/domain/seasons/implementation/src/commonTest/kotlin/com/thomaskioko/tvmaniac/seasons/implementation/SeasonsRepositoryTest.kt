package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.seasons.implementation.MockData.getShow
import com.thomaskioko.tvmaniac.seasons.implementation.MockData.getShowDetailResponse
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.core.test.testCoroutineDispatcher
import com.thomaskioko.tvmaniac.seasons.api.SeasonsCache
import com.thomaskioko.tvmaniac.discover.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.seasons.implementation.mapper.toSeasonCacheList
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlin.test.AfterTest
import kotlin.test.Ignore
import kotlin.test.Test

internal class SeasonsRepositoryTest {

    private val tvShowCache = mockk<TvShowCache>()
    private val seasonCache = mockk<SeasonsCache>()
    private val mockApiService = mockk<TvShowsService>()

    private var repository = SeasonsRepositoryImpl(
        mockApiService,
        tvShowCache,
        seasonCache,
        testCoroutineDispatcher
    )

    @AfterTest
    fun tearDownAll() {
        unmockkAll()
    }

    @Ignore
    @Test
    fun givenDataIsNotCached_shouldInvokeNetworkCallAndCache() = runBlockingTest {

        every { tvShowCache.getTvShow(showId = 84958) } returns flowOf(getShow())
        coEvery { mockApiService.getTvShowDetails(84958) } answers { getShowDetailResponse() }

        val seasonList = getShowDetailResponse().toSeasonCacheList()

        repository.observeShowSeasons(84958)

        coVerify {
            mockApiService.getTvShowDetails(84958)

            tvShowCache.updateShowDetails(
                showId = 84958,
                seasonIds = listOf(114355),
                showStatus = "Returning  Series"
            )

            seasonCache.insert(seasonList)
            seasonCache.observeSeasons(84958)
        }
    }
}
