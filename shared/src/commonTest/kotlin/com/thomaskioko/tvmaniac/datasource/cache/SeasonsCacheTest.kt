package com.thomaskioko.tvmaniac.datasource.cache

import com.thomaskioko.tvmaniac.MockData.getEpisodeEntityList
import com.thomaskioko.tvmaniac.MockData.tvSeasonsList
import com.thomaskioko.tvmaniac.presentation.model.Season
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class SeasonsCacheTest : BaseDatabaseTest() {

    private val tvSeasonQueries get() = database.seasonQueries

    @Test
    fun insertSeason_andSeasonBySeasonId_returnsExpectedData() {

        tvSeasonsList.insertSeasonsEntityQuery()

        val queryResult = tvSeasonQueries.selectBySeasonId(114355).executeAsOne()

        queryResult.id shouldBe tvSeasonsList[0].seasonId
        queryResult.tv_show_id shouldBe tvSeasonsList[0].tvShowId
        queryResult.name shouldBe tvSeasonsList[0].name
        queryResult.season_number shouldBe tvSeasonsList[0].seasonNumber
    }

    @Test
    fun insertSeason_andSelectSeasonsByShowId_returnsExpectedData() {

        tvSeasonsList.insertSeasonsEntityQuery()

        val queryResult = tvSeasonQueries.selectSeasonsByShowId(84958).executeAsList()

        queryResult shouldNotBe null
        queryResult.size shouldBe 2
    }

    @Test
    fun givenUpdateEpisodes_queryReturnsCorrectData() {

        tvSeasonsList.insertSeasonsEntityQuery()

        val queryResult = tvSeasonQueries.selectBySeasonId(114355).executeAsOne()

        //Verify that the first time the list is empty
        queryResult.episodes shouldBe null

        tvSeasonQueries.updateEpisodes(
            id = 114355,
            episodes = getEpisodeEntityList()
        )

        val episodeResult = tvSeasonQueries.selectBySeasonId(114355)
            .executeAsOne()
            .episodes

        //Verify that the list has been updated and exists
        episodeResult shouldBe getEpisodeEntityList()

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