package com.thomaskioko.tvmaniac.core.db

import com.thomaskioko.tvmaniac.core.db.MockData.getEpisodeCacheList
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class EpisodesCacheTest : BaseDatabaseTest() {

    private val episodeQueries get() = database.episodeQueries

    @Test
    fun insertEpisodes_andEpisodeByEpisodeId_returnsExpectedData() {

        getEpisodeCacheList().insertEpisodeEntityQuery()
        val entity = getEpisodeCacheList().first()

        val queryResult = episodeQueries.episodeById(2534997).executeAsOne()

        queryResult.id shouldBe entity.id
        queryResult.season_id shouldBe entity.season_id
        queryResult.title shouldBe entity.title
        queryResult.overview shouldBe entity.overview
        queryResult.image_url shouldBe entity.image_url
        queryResult.vote_average shouldBe entity.vote_average
        queryResult.votes shouldBe entity.votes
    }

    @Test
    fun insertEpisodes_andSelectEpisodesBySeasonId_returnsExpectedData() {

        getEpisodeCacheList().insertEpisodeEntityQuery()

        val queryResult = episodeQueries.episodesBySeasonId(114355).executeAsList()

        queryResult shouldNotBe null
        queryResult.size shouldBe 2
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
            image_url = image_url,
            votes = votes,
            vote_average = vote_average,
            episode_number = episode_number,
            tmdb_id = tmdb_id
        )
    }
}
