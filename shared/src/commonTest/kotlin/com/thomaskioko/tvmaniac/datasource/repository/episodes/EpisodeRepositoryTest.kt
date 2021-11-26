package com.thomaskioko.tvmaniac.datasource.repository.episodes

import com.thomaskioko.tvmaniac.MockData.getEpisodeCache
import com.thomaskioko.tvmaniac.MockData.getEpisodeCacheList
import com.thomaskioko.tvmaniac.MockData.getEpisodesBySeasonId
import com.thomaskioko.tvmaniac.MockData.getSeasonCache
import com.thomaskioko.tvmaniac.MockData.getShowSeasonsResponse
import com.thomaskioko.tvmaniac.datasource.cache.episode.EpisodesCache
import com.thomaskioko.tvmaniac.datasource.cache.seasons.SeasonsCache
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepositoryImpl
import com.thomaskioko.tvmaniac.presentation.model.Episode
import com.thomaskioko.tvmaniac.util.runBlocking
import io.kotest.matchers.shouldNotBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
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
        repository = EpisodeRepositoryImpl(apiService, episodesCache, seasonCache)
    }

    @AfterTest
    fun tearDownAll() {
        unmockkAll()
    }

    @Test
    fun givenGetEpisodeByEpisodeIdIsInvoked_DataIsLoadedFromCache() = runBlocking {
        val episodeNumber = 2534997
        every {
            episodesCache.getEpisodeByEpisodeId(episodeNumber)
        } returns getEpisodeCache()

        val result = repository.getEpisodeByEpisodeId(episodeNumber)

        result shouldNotBe emptyList<Episode>()

        verify { episodesCache.getEpisodeByEpisodeId(episodeNumber) }
    }

    @Test
    fun givenDataIsCached_thenGetEpisodesBySeasonIdDataIsLoadedFromCache() = runBlocking {
        every {
            episodesCache.getEpisodesBySeasonId(seasonId)
        } returns getEpisodesBySeasonId()

        val result = repository.getEpisodesBySeasonId(showId, seasonId, 1)

        result shouldNotBe emptyList<Episode>()

        verify(exactly = 0) {
            runBlocking { apiService.getSeasonDetails(seasonId, 1) }
            episodesCache.insert(getEpisodeCacheList())
        }

        verify(exactly = 2) { episodesCache.getEpisodesBySeasonId(seasonId) }
    }

    @Test
    fun givenDataIsNotCached_thenGetEpisodesBySeasonId_shouldInvokeApiAndCacheResult() =
        runBlocking {

            coEvery { apiService.getSeasonDetails(showId, 1) } answers { getShowSeasonsResponse() }
            every { seasonCache.getSeasonBySeasonId(seasonId) } returns getSeasonCache()

            repository.getEpisodesBySeasonId(showId, seasonId, 1)

            verify {
                runBlocking { apiService.getSeasonDetails(showId, 1) }

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
