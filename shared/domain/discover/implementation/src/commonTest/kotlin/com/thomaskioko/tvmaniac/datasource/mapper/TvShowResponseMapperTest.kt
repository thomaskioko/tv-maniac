package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.MockData.getTvResponse
import com.thomaskioko.tvmaniac.discover.implementation.toShow
import com.thomaskioko.tvmaniac.shared.core.util.StringUtil.formatPosterPath
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
        mappedShow.id shouldBe showResponse.id
        mappedShow.title shouldBe showResponse.name
        mappedShow.description shouldBe showResponse.overview
        mappedShow.poster_image_url shouldBe formatPosterPath(showResponse.posterPath)
        mappedShow.votes shouldBe showResponse.voteCount
        mappedShow.vote_average shouldBe showResponse.voteAverage
        mappedShow.genre_ids shouldBe showResponse.genreIds
    }
}
