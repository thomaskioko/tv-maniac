package com.thomaskioko.tvmaniac.domain.widget

import io.kotest.matchers.shouldBe
import java.io.File
import kotlin.test.Test

class WidgetSnapshotFixtureTest {

    private val fixture = File(FIXTURE_PATH)

    private val expected = WidgetSnapshot(
        writtenAtMillis = 1_724_400_000_000,
        entries = listOf(
            WidgetSnapshotEntry(
                tmdbId = 1396,
                showName = "Breaking Bad",
                episodeName = "Pilot",
                seasonNumber = 1,
                episodeNumber = 1,
                posterFileName = "1396.jpg",
            ),
            WidgetSnapshotEntry(
                tmdbId = 60059,
                showName = "Better Call Saul",
                episodeName = "Uno",
                seasonNumber = 1,
                episodeNumber = 1,
                posterFileName = null,
            ),
        ),
    )

    @Test
    fun `should find the fixture the Swift test reads`() {
        fixture.exists() shouldBe true
    }

    @Test
    fun `should decode the fixture the Swift test reads`() {
        WidgetSnapshotJson.decode(fixture.readText()) shouldBe expected
    }

    @Test
    fun `should encode to what the fixture holds`() {
        val encoded = WidgetSnapshotJson.encode(expected)

        WidgetSnapshotJson.decode(encoded) shouldBe WidgetSnapshotJson.decode(fixture.readText())
    }

    private companion object {
        const val FIXTURE_PATH = "../../ios/Packages/Models/Tests/ModelsTests/Fixtures/widget-snapshot.json"
    }
}
