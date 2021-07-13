package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.MockData.getTvResponse
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowCategory.POPULAR_TV_SHOWS
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class TvShowResponseMapperTest {

    @Test
    fun givenApiResponse_VerifyResponse_isMappedToEntityList() {

        val response = getTvResponse()
        val mappedData = response.results.map { it.toTvShowEntityList(POPULAR_TV_SHOWS) }

        val showResponse = response.results.first()
        val mappedShow = mappedData.first()

        mappedData.size shouldBe response.results.size
        mappedShow.showId shouldBe showResponse.id
        mappedShow.title shouldBe showResponse.name
        mappedShow.description shouldBe showResponse.overview
        mappedShow.imageUrl shouldBe showResponse.backdropPath
        mappedShow.votes shouldBe showResponse.voteCount
        mappedShow.averageVotes shouldBe showResponse.voteAverage
        mappedShow.genreIds shouldBe showResponse.genreIds
        mappedShow.showCategory shouldBe POPULAR_TV_SHOWS
    }
}