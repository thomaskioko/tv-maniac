package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class EpisodesCacheTest : BaseDatabaseTest() {

  private val episodeQueries
    get() = database.episodesQueries

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

  private fun getEpisodeCacheList() =
    listOf(
      Episode(
        id = Id(2534997),
        season_id = Id(114355),
        show_id = Id(123232),
        title = "Glorious Purpose",
        overview =
        "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
        vote_count = 42,
        vote_average = 6.429,
        runtime = 45,
        episode_number = 1,
        image_url = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
      ),
      Episode(
        id = Id(2927202),
        season_id = Id(114355),
        show_id = Id(123232),
        title = "The Variant",
        overview =
        "Mobius puts Loki to work, but not everyone at TVA is thrilled about the God of Mischief's presence.",
        vote_count = 42,
        vote_average = 6.429,
        runtime = 45,
        episode_number = 1,
        image_url = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
      ),
    )
}
