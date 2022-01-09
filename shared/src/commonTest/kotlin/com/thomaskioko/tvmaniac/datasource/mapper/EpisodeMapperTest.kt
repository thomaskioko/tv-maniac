package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.MockData.getShowSeasonsResponse
import com.thomaskioko.tvmaniac.shared.core.util.StringUtil.formatPosterPath
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class EpisodeMapperTest {

    @Test
    fun givenApiResponse_VerifyResponse_isMappedToEntity() {

        val response = getShowSeasonsResponse()
        val mappedData = response.toEpisodeEntityList()

        val episodeResponse = response.episodes.first()
        val mappedEpisode = mappedData.first()

        mappedEpisode.seasonId shouldBe response.id
        mappedEpisode.id shouldBe episodeResponse.id
        mappedEpisode.seasonNumber shouldBe episodeResponse.season_number
        mappedEpisode.name shouldBe episodeResponse.name
        mappedEpisode.imageUrl shouldBe formatPosterPath(episodeResponse.still_path)
        mappedEpisode.episodeNumber shouldBe episodeResponse.episode_number.toString().padStart(2, '0')
        mappedEpisode.voteAverage shouldBe episodeResponse.vote_average
        mappedEpisode.voteCount shouldBe episodeResponse.vote_count
        mappedEpisode.overview shouldBe episodeResponse.overview
    }
}
