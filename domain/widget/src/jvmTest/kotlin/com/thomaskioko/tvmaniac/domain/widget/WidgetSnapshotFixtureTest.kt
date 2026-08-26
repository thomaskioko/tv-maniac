package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.domain.widget.model.WidgetSnapshot
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetSnapshotEntry
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.test.Test

class WidgetSnapshotFixtureTest {

    private val json = Json { ignoreUnknownKeys = true }
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
    fun `should find the fixture file`() {
        fixture.exists() shouldBe true
    }

    @Test
    fun `should read the values the swift test also reads`() {
        json.decodeFromString<WidgetSnapshot>(fixture.readText()) shouldBe expected
    }

    private companion object {
        const val FIXTURE_PATH = "../../ios/Packages/Models/Tests/ModelsTests/Fixtures/widget-snapshot.json"
    }
}
