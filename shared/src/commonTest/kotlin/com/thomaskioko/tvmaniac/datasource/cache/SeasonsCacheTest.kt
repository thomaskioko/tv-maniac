package com.thomaskioko.tvmaniac.datasource.cache

import com.thomaskioko.tvmaniac.MockData.tvSeasonsList
import com.thomaskioko.tvmaniac.datasource.cache.model.SeasonsEntity
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class SeasonsCacheTest : BaseDatabaseTest() {

    private val tvSeasonQueries get() = database.tvSeasonQueries

    @Test
    fun insertSeason_andSeasonBySeasonId_returnsExpectedData() {

        tvSeasonsList.insertSeasonsEntityQuery()

        val queryResult = tvSeasonQueries.selectBySeasonId(114355).executeAsOne()

        queryResult.season_id shouldBe tvSeasonsList[0].seasonId
        queryResult.tv_show_id shouldBe tvSeasonsList[0].tvShowId
        queryResult.name shouldBe tvSeasonsList[0].name
    }

    @Test
    fun insertSeason_andSelectSeasonsByShowId_returnsExpectedData() {

        tvSeasonsList.insertSeasonsEntityQuery()

        val queryResult = tvSeasonQueries.selectSeasonsByShowId(84958).executeAsList()

        queryResult shouldNotBe null
        queryResult.size shouldBe 2
    }


    private fun List<SeasonsEntity>.insertSeasonsEntityQuery() {
        map { it.insertSeasonsEntityQuery() }
    }

    private fun SeasonsEntity.insertSeasonsEntityQuery() {
        database.tvSeasonQueries.insertOrReplace(
            season_id = seasonId.toLong(),
            tv_show_id = tvShowId.toLong(),
            season_number = seasonId.toLong(),
            epiosode_count = episodeCount.toLong(),
            name = name,
            overview = overview,
        )
    }
}