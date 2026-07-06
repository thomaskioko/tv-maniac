package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.episodes.api.model.ShowMetadataSyncInfo
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ShowMetadataSyncHelperTest {

    private val episodeRepository = FakeEpisodeRepository()
    private val helper = ShowMetadataSyncHelper(episodeRepository)

    @Test
    fun `should not sync given ended show with complete local episode data`() = runTest {
        episodeRepository.setShowMetadataSyncInfo(
            showId = SHOW_ID,
            info = ShowMetadataSyncInfo(status = "Ended", metadataEpisodeCount = 62, localEpisodeCount = 62),
        )

        helper.shouldSync(SHOW_ID) shouldBe false
    }

    @Test
    fun `should not sync given canceled show reported with provider lowercase status`() = runTest {
        episodeRepository.setShowMetadataSyncInfo(
            showId = SHOW_ID,
            info = ShowMetadataSyncInfo(status = "canceled", metadataEpisodeCount = 10, localEpisodeCount = 10),
        )

        helper.shouldSync(SHOW_ID) shouldBe false
    }

    @Test
    fun `should sync without latest season refresh given ended show with missing local episodes`() = runTest {
        episodeRepository.setShowMetadataSyncInfo(
            showId = SHOW_ID,
            info = ShowMetadataSyncInfo(status = "Ended", metadataEpisodeCount = 62, localEpisodeCount = 12),
        )

        helper.shouldSync(SHOW_ID) shouldBe true
        helper.shouldRefreshLatestSeason(SHOW_ID) shouldBe false
    }

    @Test
    fun `should sync given ended show with no metadata episode count`() = runTest {
        episodeRepository.setShowMetadataSyncInfo(
            showId = SHOW_ID,
            info = ShowMetadataSyncInfo(status = "Ended", metadataEpisodeCount = 0, localEpisodeCount = 0),
        )

        helper.shouldSync(SHOW_ID) shouldBe true
    }

    @Test
    fun `should refresh latest season given returning show`() = runTest {
        episodeRepository.setShowMetadataSyncInfo(
            showId = SHOW_ID,
            info = ShowMetadataSyncInfo(status = "Returning Series", metadataEpisodeCount = 62, localEpisodeCount = 62),
        )

        helper.shouldSync(SHOW_ID) shouldBe true
        helper.shouldRefreshLatestSeason(SHOW_ID) shouldBe true
    }

    @Test
    fun `should refresh latest season given show with unknown status`() = runTest {
        episodeRepository.setShowMetadataSyncInfo(
            showId = SHOW_ID,
            info = ShowMetadataSyncInfo(status = null, metadataEpisodeCount = 0, localEpisodeCount = 0),
        )

        helper.shouldRefreshLatestSeason(SHOW_ID) shouldBe true
    }

    @Test
    fun `should sync without latest season refresh given show missing from local catalog`() = runTest {
        helper.shouldSync(SHOW_ID) shouldBe true
        helper.shouldRefreshLatestSeason(SHOW_ID) shouldBe false
    }

    private companion object {
        private const val SHOW_ID = 1388L
    }
}
