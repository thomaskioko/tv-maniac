package com.thomaskioko.tvmaniac.core.db

import com.thomaskioko.tvmaniac.core.db.MockData.getEpisodeCacheList
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class EpisodesCacheTest : BaseDatabaseTest() {

    private val episodeQueries get() = database.episodesQueries

    @Test
    fun insertEpisodes_andEpisodeByEpisodeId_returnsExpectedData() {

        getEpisodeCacheList().insertEpisodeEntityQuery()
        val entity = getEpisodeCacheList().first()

        val queryResult = episodeQueries.episodeById(2534997).executeAsOne()

        queryResult.trakt_id shouldBe entity.trakt_id
        queryResult.season_id shouldBe entity.season_id
        queryResult.title shouldBe entity.title
        queryResult.overview shouldBe entity.overview
        queryResult.ratings shouldBe entity.ratings
        queryResult.votes shouldBe entity.votes
    }

    private fun List<Episodes>.insertEpisodeEntityQuery() {
        map { it.insertEpisodeEntityQuery() }
    }

    private fun Episodes.insertEpisodeEntityQuery() {
        episodeQueries.insertOrReplace(
            trakt_id = trakt_id,
            season_id = season_id,
            title = title,
            overview = overview,
            votes = votes,
            episode_number = episode_number,
            tmdb_id = tmdb_id,
            ratings = ratings,
            runtime = runtime
        )
    }
}
