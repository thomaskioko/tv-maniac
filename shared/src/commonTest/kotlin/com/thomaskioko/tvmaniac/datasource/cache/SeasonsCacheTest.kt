package com.thomaskioko.tvmaniac.datasource.cache

import com.thomaskioko.tvmaniac.MockData.getSeasonsList
import com.thomaskioko.tvmaniac.presentation.model.Season
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class SeasonsCacheTest : BaseDatabaseTest() {

    private val tvSeasonQueries get() = database.tvSeasonQueries

    @Test
    fun insertSeason_andSeasonBySeasonId_returnsExpectedData() {

        getSeasonsList().insertSeasonsEntityQuery()

        val queryResult = tvSeasonQueries.selectBySeasonId(114355).executeAsOne()

        queryResult.id shouldBe getSeasonsList()[0].seasonId
        queryResult.tv_show_id shouldBe getSeasonsList()[0].tvShowId
        queryResult.name shouldBe getSeasonsList()[0].name
        queryResult.season_number shouldBe getSeasonsList()[0].seasonNumber
    }

    @Test
    fun insertSeason_andSelectSeasonsByShowId_returnsExpectedData() {

        getSeasonsList().insertSeasonsEntityQuery()

        val queryResult = tvSeasonQueries.selectSeasonsByShowId(84958).executeAsList()

        queryResult shouldNotBe null
        queryResult.size shouldBe 2
    }

    @Test
    fun givenUpdateEpisodes_queryReturnsCorrectData() {

        getSeasonsList().insertSeasonsEntityQuery()

        val queryResult = tvSeasonQueries.selectBySeasonId(114355).executeAsOne()

        // Verify that the first time the list is empty
        queryResult.episode_ids shouldBe null

        tvSeasonQueries.updateEpisodes(
            id = 114355,
            episode_ids = listOf(2534997, 2927202)
        )

        val seasonQueryResult = tvSeasonQueries.selectBySeasonId(114355)
            .executeAsOne()

        // Verify that the list has been updated and exists
        seasonQueryResult.episode_ids shouldBe listOf(2534997, 2927202)
    }

    private fun List<Season>.insertSeasonsEntityQuery() {
        map { it.insertSeasonsEntityQuery() }
    }

    private fun Season.insertSeasonsEntityQuery() {
        tvSeasonQueries.insertOrReplace(
            id = seasonId.toLong(),
            tv_show_id = tvShowId.toLong(),
            season_number = seasonNumber.toLong(),
            epiosode_count = episodeCount.toLong(),
            name = name,
            overview = overview,
        )
    }
}
