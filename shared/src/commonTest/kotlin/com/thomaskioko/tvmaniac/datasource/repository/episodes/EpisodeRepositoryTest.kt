package com.thomaskioko.tvmaniac.datasource.repository.episodes

import com.thomaskioko.tvmaniac.MockData.getEpisodeCacheList
import com.thomaskioko.tvmaniac.MockData.getSeasonCache
import com.thomaskioko.tvmaniac.MockData.getShowSeasonsResponse
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.datasource.cache.episode.EpisodesCache
import com.thomaskioko.tvmaniac.datasource.cache.seasons.SeasonsCache
import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepositoryImpl
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test

class EpisodeRepositoryTest {

    @RelaxedMockK
    lateinit var episodesCache: EpisodesCache

    @RelaxedMockK
    lateinit var seasonCache: SeasonsCache

    private lateinit var repository: EpisodeRepositoryImpl

    private val seasonId = 114355
    private val showId = 84958

    private val apiService: TvShowsService = mockk {
        coEvery { getSeasonDetails(showId, 1) } answers { getShowSeasonsResponse() }
    }

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this)
        repository = EpisodeRepositoryImpl(apiService, episodesCache, seasonCache, mockk())
    }

    @AfterTest
    fun tearDownAll() {
        unmockkAll()
    }

    @Ignore
    @Test
    fun givenDataIsNotCached_thenGetEpisodesBySeasonId_shouldInvokeApiAndCacheResult() =
        runBlockingTest {

            coEvery { apiService.getSeasonDetails(showId, 1) } answers { getShowSeasonsResponse() }
            every { seasonCache.getSeasonBySeasonId(seasonId) } returns getSeasonCache()

            repository.observeSeasonEpisodes(showId, seasonId, 1)

            coVerify {
                apiService.getSeasonDetails(showId, 1)

                // Episodes are inserted
                episodesCache.insert(getEpisodeCacheList())

                // Season Episodes are updated
                seasonCache.updateSeasonEpisodesIds(
                    seasonId = seasonId,
                    episodeIds = listOf(2534997, 2927202)
                )
            }
        }
}
