package com.thomaskioko.tvmaniac.core.db

import com.thomaskioko.tvmaniac.core.db.MockData.makeLastEpisodeList
import com.thomaskioko.tvmaniac.core.db.Last_episode
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class LastAirEpisodeCacheTest : BaseDatabaseTest() {

    private val lastAirEpisodeQueries get() = database.lastAirEpisodeQueries

    @Test
    fun givenShowsAreInserted_verifyQuery_returnsExpectedResult() {
        makeLastEpisodeList().insertEntityList()

        val entities = lastAirEpisodeQueries.airEpisodesByShowId(84958).executeAsList()

        entities.size shouldBe 2
    }

    private fun List<Last_episode>.insertEntityList() {
        map { it.insertTvShowQuery() }
    }

    private fun Last_episode.insertTvShowQuery() {
        lastAirEpisodeQueries.insertOrReplace(
            id = id,
            show_id = show_id,
            name = name,
            overview = overview,
            title = title,
            air_date = air_date,
            episode_number = episode_number,
            season_number = season_number,
            still_path = still_path,
            vote_average = vote_average,
            vote_count = vote_count
        )
    }
}
