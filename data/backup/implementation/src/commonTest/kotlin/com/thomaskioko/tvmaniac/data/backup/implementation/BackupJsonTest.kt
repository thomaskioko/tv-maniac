package com.thomaskioko.tvmaniac.data.backup.implementation

import com.thomaskioko.tvmaniac.data.backup.api.BackupFile
import com.thomaskioko.tvmaniac.data.backup.api.BackupFormat
import com.thomaskioko.tvmaniac.data.backup.api.BackupShow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class BackupJsonTest {

    @Test
    fun `should fail given the file was written by a newer release`() {
        val contents = """
            {"version": 99, "createdAt": "2026-01-01T00:00:00Z", "appVersion": "9.0.0"}
        """.trimIndent()

        shouldThrow<BackupVersionTooNewException> { BackupJson.decode(contents) }.version shouldBe 99
    }

    @Test
    fun `should fail given the file has no version`() {
        val contents = """
            {"createdAt": "2026-01-01T00:00:00Z", "appVersion": "1.0.0"}
        """.trimIndent()

        shouldThrow<BackupVersionMissingException> { BackupJson.decode(contents) }
    }

    @Test
    fun `should read a file given it has an unknown field`() {
        val contents = """
            {
              "version": ${BackupFormat.VERSION},
              "createdAt": "2026-01-01T00:00:00Z",
              "appVersion": "1.0.0",
              "somethingAddedLater": true,
              "shows": [{"tmdbId": 1396, "title": "Breaking Bad", "unknownField": 3}]
            }
        """.trimIndent()

        val backup = BackupJson.decode(contents)

        backup.shows shouldHaveSize 1
        backup.shows.first().tmdbId shouldBe 1396L
    }

    @Test
    fun `should read a file given optional sections are absent`() {
        val contents = """
            {"version": ${BackupFormat.VERSION}, "createdAt": "2026-01-01T00:00:00Z", "appVersion": "1.0.0"}
        """.trimIndent()

        val backup = BackupJson.decode(contents)

        backup.shows shouldHaveSize 0
    }

    @Test
    fun `should round trip given a file holds shows and preferences`() {
        val backup = BackupFile(
            version = BackupFormat.VERSION,
            createdAt = "2026-01-01T00:00:00Z",
            appVersion = "1.0.0",
            shows = listOf(BackupShow(tmdbId = 1396L, title = "Breaking Bad")),
        )

        BackupJson.decode(BackupJson.encode(backup)) shouldBe backup
    }
}
