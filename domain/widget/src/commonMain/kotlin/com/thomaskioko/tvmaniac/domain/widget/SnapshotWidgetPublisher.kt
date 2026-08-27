package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.core.files.api.JsonFileManager
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetShow
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetSnapshot
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetSnapshotEntry
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public class SnapshotWidgetPublisher(
    private val widgetManager: WidgetManager,
    private val jsonFileManager: JsonFileManager,
    private val dateTimeProvider: DateTimeProvider,
) : WidgetPublisher {

    override suspend fun hasInstalledWidgets(): Boolean = suspendCoroutine { continuation ->
        widgetManager.hasInstalledWidgets { continuation.resume(it) }
    }

    override suspend fun publish(shows: List<WidgetShow>) {
        val directoryPath = widgetManager.containerPath() ?: return

        jsonFileManager.writeToFile(
            directoryPath = directoryPath,
            fileName = SNAPSHOT_FILE_NAME,
            value = WidgetSnapshot(
                writtenAtMillis = dateTimeProvider.nowMillis(),
                entries = shows.map { it.toEntry() },
            ),
            type = WidgetSnapshot::class,
        )

        widgetManager.reloadTimelines()
    }

    private fun WidgetShow.toEntry(): WidgetSnapshotEntry = WidgetSnapshotEntry(
        tmdbId = tmdbId,
        showName = showName,
        episodeName = episodeName,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        posterFileName = null,
    )

    public companion object {
        public const val SNAPSHOT_FILE_NAME: String = "widget-snapshot.json"
    }
}
