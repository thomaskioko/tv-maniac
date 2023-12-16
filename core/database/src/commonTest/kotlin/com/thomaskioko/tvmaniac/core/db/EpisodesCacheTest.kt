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

        val queryResult = episodeQueries.episodeDetails(Id(2534997)).executeAsOne()

        queryResult.id.id shouldBe entity.id.id
        queryResult.season_id shouldBe entity.season_id
        queryResult.title shouldBe entity.title
        queryResult.overview shouldBe entity.overview
        queryResult.vote_average shouldBe entity.vote_average
        queryResult.vote_count shouldBe entity.vote_count
    }

    private fun List<Episode>.insertEpisodeEntityQuery() {
        map { it.insertEpisodeEntityQuery() }
    }

    private fun Episode.insertEpisodeEntityQuery() {
        episodeQueries.upsert(
            id = id,
            season_id = season_id,
            title = title,
            overview = overview,
            vote_average = vote_average,
            episode_number = episode_number,
            runtime = runtime,
            show_id = show_id,
            vote_count = vote_count,
            image_url = image_url,
        )
    }
}
