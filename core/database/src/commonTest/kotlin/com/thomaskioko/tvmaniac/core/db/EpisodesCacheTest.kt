package com.thomaskioko.tvmaniac.core.db

import com.thomaskioko.tvmaniac.core.db.MockData.getEpisodeCacheList
import com.thomaskioko.tvmaniac.db.Id
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class EpisodesCacheTest : BaseDatabaseTest() {

    private val episodeQueries get() = database.episodesQueries

    @Test
    fun insertEpisodes_andEpisodeByEpisodeId_returnsExpectedData() {
        getEpisodeCacheList().insertEpisodeEntityQuery()
        val entity = getEpisodeCacheList().first()

        val queryResult = episodeQueries.episodesById(Id(2534997)).executeAsOne()

        queryResult.id.id shouldBe entity.id.id
        queryResult.season_id shouldBe entity.season_id
        queryResult.title shouldBe entity.title
        queryResult.overview shouldBe entity.overview
        queryResult.ratings shouldBe entity.ratings
        queryResult.votes shouldBe entity.votes
    }

    private fun List<Episode>.insertEpisodeEntityQuery() {
        map { it.insertEpisodeEntityQuery() }
    }

    private fun Episode.insertEpisodeEntityQuery() {
        episodeQueries.insertOrReplace(
            id = id,
            season_id = season_id,
            title = title,
            overview = overview,
            votes = votes,
            episode_number = episode_number,
            tmdb_id = tmdb_id,
            ratings = ratings,
            runtime = runtime,
        )
    }
}
