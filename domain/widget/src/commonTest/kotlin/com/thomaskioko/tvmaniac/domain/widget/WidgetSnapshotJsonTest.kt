package com.thomaskioko.tvmaniac.domain.widget

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class WidgetSnapshotJsonTest {

    @Test
    fun `should round trip six entries without loss`() {
        val snapshot = WidgetSnapshot(
            writtenAtMillis = 1_724_400_000_000,
            entries = (1L..6L).map { entry(tmdbId = it) },
        )

        WidgetSnapshotJson.decode(WidgetSnapshotJson.encode(snapshot)) shouldBe snapshot
    }

    @Test
    fun `should round trip an empty snapshot`() {
        val snapshot = WidgetSnapshot(writtenAtMillis = 0, entries = emptyList())

        WidgetSnapshotJson.decode(WidgetSnapshotJson.encode(snapshot)) shouldBe snapshot
    }

    @Test
    fun `should decode a document carrying a field it does not know`() {
        val contents = """
            {
              "writtenAtMillis": 1,
              "entries": [],
              "somethingAddedLater": "value"
            }
        """.trimIndent()

        WidgetSnapshotJson.decode(contents) shouldBe WidgetSnapshot(writtenAtMillis = 1, entries = emptyList())
    }

    @Test
    fun `should decode an entry written without a poster`() {
        val contents = """
            {
              "writtenAtMillis": 1,
              "entries": [
                {
                  "tmdbId": 1396,
                  "showName": "Breaking Bad",
                  "episodeName": "Pilot",
                  "seasonNumber": 1,
                  "episodeNumber": 1
                }
              ]
            }
        """.trimIndent()

        WidgetSnapshotJson.decode(contents).entries.single().posterFileName shouldBe null
    }

    @Test
    fun `should write the field names the Swift mirror reads`() {
        val encoded = WidgetSnapshotJson.encode(
            WidgetSnapshot(writtenAtMillis = 1, entries = listOf(entry(tmdbId = 1396))),
        )

        listOf(
            "writtenAtMillis",
            "entries",
            "tmdbId",
            "showName",
            "episodeName",
            "seasonNumber",
            "episodeNumber",
            "posterFileName",
        ).forEach { encoded shouldContain it }
    }

    private fun entry(tmdbId: Long) = WidgetSnapshotEntry(
        tmdbId = tmdbId,
        showName = "Breaking Bad",
        episodeName = "Pilot",
        seasonNumber = 1,
        episodeNumber = 1,
        posterFileName = "$tmdbId.jpg",
    )
}
