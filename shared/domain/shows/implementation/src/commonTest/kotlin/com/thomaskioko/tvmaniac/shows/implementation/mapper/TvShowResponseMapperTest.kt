package com.thomaskioko.tvmaniac.shows.implementation.mapper

import com.thomaskioko.tvmaniac.shows.implementation.MockData.getTvResponse
import com.thomaskioko.tvmaniac.core.util.FormatterUtil.formatPosterPath
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class TvShowResponseMapperTest {

    @Test
    fun givenApiResponse_VerifyResponse_isMappedToEntityList() {

        val response = getTvResponse()
        val mappedData = response.results
            .map { it.toShow() }

        val showResponse = response.results.first()
        val mappedShow = mappedData.first()

        mappedData.size shouldBe response.results.size
        mappedShow.tmdb_id shouldBe showResponse.id
        mappedShow.title shouldBe showResponse.name
        mappedShow.overview shouldBe showResponse.overview
        mappedShow.poster_image_url shouldBe formatPosterPath(showResponse.posterPath)
        mappedShow.votes shouldBe showResponse.voteCount
    }
}
