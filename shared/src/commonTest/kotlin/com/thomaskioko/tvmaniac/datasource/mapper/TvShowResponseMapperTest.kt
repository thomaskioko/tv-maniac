package com.thomaskioko.tvmaniac.datasource.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.thomaskioko.tvmaniac.MockData.getTvResponse
import kotlin.test.Test

internal class TvShowResponseMapperTest {

    @Test
    fun givenApiResponse_VerifyResponse_isMappedToEntityList() {

        val response = getTvResponse()
        val mappedData = response.results.map { it.toTvShowEntityList() }

        val showResponse = response.results.first()
        val mappedShow = mappedData.first()

        assertThat(mappedData.size).isEqualTo(response.results.size)
        assertThat(mappedShow.showId).isEqualTo(showResponse.id)
        assertThat(mappedShow.title).isEqualTo(showResponse.name)
        assertThat(mappedShow.description).isEqualTo(showResponse.overview)
        assertThat(mappedShow.imageUrl).isEqualTo(showResponse.backdropPath)
        assertThat(mappedShow.votes).isEqualTo(showResponse.voteCount)
        assertThat(mappedShow.averageVotes).isEqualTo(showResponse.voteAverage)
        assertThat(mappedShow.genreIds).isEqualTo(showResponse.genreIds)
    }
}