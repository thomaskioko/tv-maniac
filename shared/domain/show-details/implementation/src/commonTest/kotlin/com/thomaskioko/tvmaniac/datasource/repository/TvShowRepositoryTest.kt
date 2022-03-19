package com.thomaskioko.tvmaniac.datasource.repository

import com.thomaskioko.tvmaniac.MockData.getShow
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.core.test.testCoroutineDispatcher
import com.thomaskioko.tvmaniac.core.test.testCoroutineScope
import com.thomaskioko.tvmaniac.details.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.details.implementation.repository.TvShowsRepositoryImpl
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastEpisodeAirCache
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlin.test.AfterTest
import kotlin.test.Ignore
import kotlin.test.Test

internal class TvShowRepositoryTest {

    private var apiService = mockk<TvShowsService>()
    private var tvShowCache = spyk<TvShowCache>()
    private var showCategoryCache = spyk<ShowCategoryCache>()
    private var lastEpisodeAirCache = spyk<LastEpisodeAirCache>()

    private val repository: TvShowsRepositoryImpl = TvShowsRepositoryImpl(
        apiService,
        tvShowCache,
        lastEpisodeAirCache,
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
        every { tvShowCache.observeTvShow(any()) } returns flowOf(getShow())

        repository.observeShow(84958)

        coVerify(exactly = 0) {
            apiService.getTvShowDetails(any())
        }

        coVerify { tvShowCache.observeTvShow(84958) }
    }

    @Test
    fun givenDataIsCached_thenWatchlistIsFetched() = runBlockingTest {
        repository.observeFollowing()

        coVerify { tvShowCache.observeFollowing() }
    }
}
