package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.MockData.getShowDetailResponse
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class SeasonsMapperTest {

    @Test
    fun givenApiResponse_VerifyResponse_isMappedToEntityList() {

        val response = getShowDetailResponse()
        val mappedData = response.toSeasonCacheList()

        val showResponse = response.seasons.first()
        val mappedShow = mappedData.first()

        mappedData.size shouldBe response.seasons.size
        mappedShow.id shouldBe showResponse.id
        mappedShow.tv_show_id shouldBe response.id
        mappedShow.name shouldBe showResponse.name
        mappedShow.overview shouldBe showResponse.overview
        mappedShow.epiosode_count shouldBe showResponse.episodeCount
        mappedShow.season_number shouldBe showResponse.seasonNumber
    }
}