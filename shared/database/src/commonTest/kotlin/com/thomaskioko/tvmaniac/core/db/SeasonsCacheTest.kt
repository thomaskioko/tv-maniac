package com.thomaskioko.tvmaniac.core.db

import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.core.db.MockData.getSeasonCacheList
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class SeasonsCacheTest : BaseDatabaseTest() {

    private val tvSeasonQueries get() = database.seasonQueries

    @Test
    fun insertSeason_andSeasonBySeasonId_returnsExpectedData() {

        getSeasonCacheList().insertSeasonsEntityQuery()

        val queryResult = tvSeasonQueries.selectBySeasonId(114355).executeAsOne()

        queryResult.id shouldBe getSeasonCacheList()[0].id
        queryResult.tv_show_id shouldBe getSeasonCacheList()[0].tv_show_id
        queryResult.name shouldBe getSeasonCacheList()[0].name
        queryResult.season_number shouldBe getSeasonCacheList()[0].season_number
    }

    @Test
    fun insertSeason_andSelectSeasonsByShowId_returnsExpectedData() {

        getSeasonCacheList().insertSeasonsEntityQuery()

        val queryResult = tvSeasonQueries.selectSeasonsByShowId(84958).executeAsList()

        queryResult shouldNotBe null
        queryResult.size shouldBe 2
    }

    @Test
    fun givenUpdateEpisodes_queryReturnsCorrectData() {

        getSeasonCacheList().insertSeasonsEntityQuery()

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
            id = id,
            tv_show_id = tv_show_id,
            season_number = season_number,
            epiosode_count = epiosode_count,
            name = name,
            overview = overview,
        )
    }
}
