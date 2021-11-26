package com.thomaskioko.tvmaniac.datasource.repository.seasons

import com.thomaskioko.tvmaniac.MockData.getSeasonCacheList
import com.thomaskioko.tvmaniac.MockData.getSelectSeasonsByShowId
import com.thomaskioko.tvmaniac.MockData.getShow
import com.thomaskioko.tvmaniac.MockData.getShowDetailResponse
import com.thomaskioko.tvmaniac.datasource.cache.seasons.SeasonsCache
import com.thomaskioko.tvmaniac.datasource.cache.shows.TvShowCache
import com.thomaskioko.tvmaniac.datasource.mapper.toSeasonCacheList
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.presentation.model.Season
import com.thomaskioko.tvmaniac.util.runBlocking
import io.kotest.matchers.shouldNotBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class SeasonsRepositoryTest {

    @RelaxedMockK
    lateinit var tvShowCache: TvShowCache

    @RelaxedMockK
    lateinit var seasonCache: SeasonsCache

    private lateinit var repository: SeasonsRepositoryImpl

    private val apiService: TvShowsService = mockk {
        coEvery { getTvShowDetails(84958) } answers { getShowDetailResponse() }
    }

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this)
        repository = SeasonsRepositoryImpl(apiService, tvShowCache, seasonCache)
    }

    @AfterTest
    fun tearDownAll() {
        unmockkAll()
    }

    @Test
    fun givenDataIsCached_thenGetTvShowSeasonsDataIsLoadedFromCache() = runBlocking {
        every { seasonCache.getSeasonsByTvShowId(84958) } returns getSelectSeasonsByShowId()

        val result = repository.getSeasonListByTvShowId(84958)

        result shouldNotBe emptyList<Season>()

        verify(exactly = 0) {
            runBlocking { apiService.getTvShowDetails(1) }
            seasonCache.insert(getSeasonCacheList())
        }

        verify(exactly = 2) { seasonCache.getSeasonsByTvShowId(84958) }
    }

    @Test
    fun givenDataIsNotCached_thenGetTvSeasonDetailsIsInvoked_AndDataIsLoadedFromCache() =
        runBlocking {
            coEvery { apiService.getTvShowDetails(84958) } answers { getShowDetailResponse() }
            every { tvShowCache.getTvShow(showId = 84958) } returns flowOf(getShow())

            val seasonList = getShowDetailResponse().toSeasonCacheList()

            repository.getSeasonListByTvShowId(84958)

            verify {
                runBlocking { apiService.getTvShowDetails(84958) }

                tvShowCache.updateShowDetails(
                    showId = 84958,
                    seasonIds = listOf(114355),
                    showStatus = "Returning  Series"
                )
                seasonCache.insert(seasonList)
                seasonCache.getSeasonsByTvShowId(84958)
            }
        }
}
