package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.core.files.implementation.DefaultJsonFileManager
import com.thomaskioko.tvmaniac.core.files.testing.FakeFileManager
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetShow
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetSnapshot
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SnapshotWidgetPublisherTest {

    private val directory = "/containers/group.com.thomaskioko.tvmaniac"
    private val bridge = FakeWidgetManager()
    private val fileManager = FakeFileManager()
    private val jsonFileManager = DefaultJsonFileManager(fileManager)
    private val publisher = SnapshotWidgetPublisher(
        widgetManager = bridge,
        jsonFileManager = jsonFileManager,
        dateTimeProvider = FakeDateTimeProvider(),
    )

    @Test
    fun `should report widgets are installed given one is added`() = runTest {
        bridge.setInstalled(true)

        publisher.hasInstalledWidgets() shouldBe true
    }

    @Test
    fun `should report no widgets given none is added`() = runTest {
        bridge.setInstalled(false)

        publisher.hasInstalledWidgets() shouldBe false
    }

    @Test
    fun `should write the shows given a container exists`() = runTest {
        bridge.setContainerPath(directory)

        publisher.publish(listOf(show()))

        val written = jsonFileManager.getFileContent(directory, SnapshotWidgetPublisher.SNAPSHOT_FILE_NAME, WidgetSnapshot::class)
        written?.entries?.single()?.showName shouldBe "Breaking Bad"
    }

    @Test
    fun `should reload the widget given the shows were written`() = runTest {
        bridge.setContainerPath(directory)

        publisher.publish(listOf(show()))

        bridge.getReloadCount() shouldBe 1
    }

    @Test
    fun `should write nothing given no container exists`() = runTest {
        bridge.setContainerPath(null)

        publisher.publish(listOf(show()))

        fileManager.contentsOf(directory, SnapshotWidgetPublisher.SNAPSHOT_FILE_NAME) shouldBe null
        bridge.getReloadCount() shouldBe 0
    }

    @Test
    fun `should write an empty list given no shows`() = runTest {
        bridge.setContainerPath(directory)

        publisher.publish(emptyList())

        val written = jsonFileManager.getFileContent(directory, SnapshotWidgetPublisher.SNAPSHOT_FILE_NAME, WidgetSnapshot::class)
        written?.entries shouldBe emptyList()
    }

    private fun show() = WidgetShow(
        tmdbId = 1396,
        showName = "Breaking Bad",
        episodeName = "Pilot",
        seasonNumber = 1,
        episodeNumber = 1,
        posterUrl = "/poster.jpg",
    )
}
