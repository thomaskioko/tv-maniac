package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.MockData.getTvResponse
import com.thomaskioko.tvmaniac.util.StringUtil.formatPosterPath
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class TvShowResponseMapperTest {

    @Test
    fun givenApiResponse_VerifyResponse_isMappedToEntityList() {

        val response = getTvResponse()
        val mappedData = response.results
            .map { it.toTvShow() }

        val showResponse = response.results.first()
        val mappedShow = mappedData.first()

        mappedData.size shouldBe response.results.size
        mappedShow.id shouldBe showResponse.id
        mappedShow.title shouldBe showResponse.name
        mappedShow.overview shouldBe showResponse.overview
        mappedShow.posterImageUrl shouldBe formatPosterPath(showResponse.posterPath)
        mappedShow.votes shouldBe showResponse.voteCount
        mappedShow.averageVotes shouldBe showResponse.voteAverage
        mappedShow.genreIds shouldBe showResponse.genreIds
    }
}
