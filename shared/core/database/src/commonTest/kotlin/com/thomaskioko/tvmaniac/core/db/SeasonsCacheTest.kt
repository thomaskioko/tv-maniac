package com.thomaskioko.tvmaniac.core.db

import com.thomaskioko.tvmaniac.core.db.MockData.getSeasonCacheList
import com.thomaskioko.tvmaniac.core.db.Season
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
        queryResult.trakt_id shouldBe getSeasonCacheList()[0].trakt_id
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

    private fun List<Season>.insertSeasonsEntityQuery() {
        map { it.insertSeasonsEntityQuery() }
    }

    private fun Season.insertSeasonsEntityQuery() {
        tvSeasonQueries.insertOrReplace(
            id = id,
            trakt_id = trakt_id,
            season_number = season_number,
            epiosode_count = epiosode_count,
            name = name,
            overview = overview,
        )
    }
}
