package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.MockData.getShowDetailResponse
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class SeasonsMapperTest {

    @Test
    fun givenApiResponse_VerifyResponse_isMappedToEntityList() {

        val response = getShowDetailResponse()
        val mappedData = response.toSeasonsEntityList()

        val showResponse = response.seasons.first()
        val mappedShow = mappedData.first()

        mappedData.size shouldBe response.seasons.size
        mappedShow.tvShowId shouldBe response.id
        mappedShow.seasonId shouldBe showResponse.id
        mappedShow.name shouldBe showResponse.name
        mappedShow.overview shouldBe showResponse.overview
        mappedShow.episodeCount shouldBe showResponse.episodeCount
        mappedShow.seasonNumber shouldBe showResponse.seasonNumber
    }
}