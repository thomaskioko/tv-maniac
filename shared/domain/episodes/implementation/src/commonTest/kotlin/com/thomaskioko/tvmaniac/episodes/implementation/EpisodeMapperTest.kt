package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.episodes.implementation.MockData.getShowSeasonsResponse
import com.thomaskioko.tvmaniac.shared.core.util.StringUtil.formatPosterPath
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class EpisodeMapperTest {

    @Test
    fun givenApiResponse_VerifyResponse_isMappedToEntity() {

        val response = getShowSeasonsResponse()
        val episodeResponse = response.episodes.first()

        val mappedData = response.toEpisodeCacheList()
        val mappedEpisode = mappedData.first()

        mappedEpisode.id shouldBe episodeResponse.id
        mappedEpisode.name shouldBe episodeResponse.name
        mappedEpisode.image_url shouldBe formatPosterPath(episodeResponse.still_path)
        mappedEpisode.episode_number shouldBe episodeResponse.episode_number.toString().padStart(2, '0')
        mappedEpisode.vote_average shouldBe episodeResponse.vote_average
        mappedEpisode.vote_count shouldBe episodeResponse.vote_count
        mappedEpisode.overview shouldBe episodeResponse.overview
    }
}
