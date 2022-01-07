package com.thomaskioko.tvmaniac.core.db

import com.thomaskioko.tvmaniac.datasource.cache.Episode
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
        queryResult.name shouldBe entity.name
        queryResult.overview shouldBe entity.overview
        queryResult.episode_season_number shouldBe entity.episode_season_number
        queryResult.image_url shouldBe entity.image_url
        queryResult.vote_average shouldBe entity.vote_average
        queryResult.vote_count shouldBe entity.vote_count
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
            episode_season_number = episode_season_number,
            name = name,
            overview = overview,
            image_url = image_url,
            vote_count = vote_count,
            vote_average = vote_average,
            episode_number = episode_number
        )
    }
}
