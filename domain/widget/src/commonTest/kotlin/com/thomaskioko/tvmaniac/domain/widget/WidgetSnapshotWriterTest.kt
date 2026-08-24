package com.thomaskioko.tvmaniac.domain.widget

import io.kotest.matchers.shouldBe
import okio.FileSystem
import kotlin.test.AfterTest
import kotlin.test.Test

class WidgetSnapshotWriterTest {

    private val directory = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "widget-snapshot-writer-test"
    private val writer = DefaultWidgetSnapshotWriter()

    @AfterTest
    fun tearDown() {
        FileSystem.SYSTEM.deleteRecursively(directory, mustExist = false)
    }

    @Test
    fun `should write a snapshot that decodes back to what was given`() {
        val snapshot = WidgetSnapshot(
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
            ),
        )

        writer.write(directory.toString(), snapshot)

        val path = directory / DefaultWidgetSnapshotWriter.SNAPSHOT_FILE_NAME
        val contents = FileSystem.SYSTEM.read(path) { readUtf8() }
        WidgetSnapshotJson.decode(contents) shouldBe snapshot
    }

    @Test
    fun `should create the directory given it does not exist yet`() {
        val nested = directory / "nested"

        writer.write(nested.toString(), WidgetSnapshot(writtenAtMillis = 0, entries = emptyList()))

        FileSystem.SYSTEM.exists(nested / DefaultWidgetSnapshotWriter.SNAPSHOT_FILE_NAME) shouldBe true
    }

    @Test
    fun `should replace a snapshot written earlier`() {
        val first = WidgetSnapshot(writtenAtMillis = 1, entries = emptyList())
        val second = WidgetSnapshot(
            writtenAtMillis = 2,
            entries = listOf(
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

        writer.write(directory.toString(), first)
        writer.write(directory.toString(), second)

        val path = directory / DefaultWidgetSnapshotWriter.SNAPSHOT_FILE_NAME
        WidgetSnapshotJson.decode(FileSystem.SYSTEM.read(path) { readUtf8() }) shouldBe second
    }
}
