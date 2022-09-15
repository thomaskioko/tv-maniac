package com.thomaskioko.tvmaniac.datasource.repository

import com.thomaskioko.tvmaniac.MockData.getShow
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.core.test.testCoroutineDispatcher
import com.thomaskioko.tvmaniac.core.test.testCoroutineScope
import com.thomaskioko.tvmaniac.details.implementation.repository.TmdbRepositoryImpl
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastEpisodeAirCache
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
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

    private var apiService = mockk<TmdbService>()
    private var tvShowCache = spyk<TvShowCache>()
    private var lastEpisodeAirCache = spyk<LastEpisodeAirCache>()

    private val repository: TmdbRepositoryImpl = TmdbRepositoryImpl(
        apiService,
        tvShowCache,
        lastEpisodeAirCache,
        testCoroutineScope,
        testCoroutineDispatcher,
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

}
