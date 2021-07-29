package com.thomaskioko.tvmaniac.datasource.cache

import com.thomaskioko.tvmaniac.MockData.getEpisodeList
import com.thomaskioko.tvmaniac.presentation.model.Episode
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class EpisodesCacheTest : BaseDatabaseTest() {

    private val episodeQueries get() = database.episodeQueries

    @Test
    fun insertEpisodes_andEpisodeByEpisodeId_returnsExpectedData() {

        getEpisodeList().insertEpisodeEntityQuery()
        val entity = getEpisodeList().first()

        val queryResult = episodeQueries.episodeById(2534997).executeAsOne()

        queryResult.id shouldBe entity.id
        queryResult.season_id shouldBe entity.seasonId
        queryResult.name shouldBe entity.name
        queryResult.overview shouldBe entity.overview
        queryResult.episode_season_number shouldBe entity.seasonNumber
        queryResult.image_url shouldBe entity.imageUrl
        queryResult.vote_average shouldBe entity.voteAverage
        queryResult.vote_count shouldBe entity.voteCount
    }

    @Test
    fun insertEpisodes_andSelectEpisodesBySeasonId_returnsExpectedData() {

        getEpisodeList().insertEpisodeEntityQuery()

        val queryResult = episodeQueries.episodesBySeasonId(114355).executeAsList()

        queryResult shouldNotBe null
        queryResult.size shouldBe 2
    }


    private fun List<Episode>.insertEpisodeEntityQuery() {
        map { it.insertEpisodeEntityQuery() }
    }

    private fun Episode.insertEpisodeEntityQuery() {
        episodeQueries.insertOrReplace(
            id = id.toLong(),
            season_id = seasonId.toLong(),
            episode_season_number = seasonNumber.toLong(),
            name = name,
            overview = overview,
            image_url = imageUrl,
            vote_count = voteCount.toLong(),
            vote_average = voteAverage,
            episode_number = episodeNumber
        )
    }
}