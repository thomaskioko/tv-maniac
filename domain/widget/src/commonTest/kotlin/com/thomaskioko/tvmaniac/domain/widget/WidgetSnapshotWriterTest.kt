package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.core.filestore.testing.FakeFileStore
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class WidgetSnapshotWriterTest {

    private val directory = "/containers/group.com.thomaskioko.tvmaniac"
    private val fileStore = FakeFileStore()
    private val writer = DefaultWidgetSnapshotWriter(fileStore)

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

        writer.write(directory, snapshot)

        val contents = fileStore.contentsOf(directory, DefaultWidgetSnapshotWriter.SNAPSHOT_FILE_NAME)
        WidgetSnapshotJson.decode(checkNotNull(contents)) shouldBe snapshot
    }

    @Test
    fun `should write an empty snapshot given an empty watchlist`() {
        writer.write(directory, WidgetSnapshot(writtenAtMillis = 0, entries = emptyList()))

        val contents = fileStore.contentsOf(directory, DefaultWidgetSnapshotWriter.SNAPSHOT_FILE_NAME)
        WidgetSnapshotJson.decode(checkNotNull(contents)).entries shouldBe emptyList()
    }

    @Test
    fun `should replace a snapshot written earlier`() {
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

        writer.write(directory, WidgetSnapshot(writtenAtMillis = 1, entries = emptyList()))
        writer.write(directory, second)

        val contents = fileStore.contentsOf(directory, DefaultWidgetSnapshotWriter.SNAPSHOT_FILE_NAME)
        WidgetSnapshotJson.decode(checkNotNull(contents)) shouldBe second
    }
}
