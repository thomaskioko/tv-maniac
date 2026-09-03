package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.core.files.api.JsonFileManager
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetShow
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetSnapshot
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetSnapshotEntry
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public class SnapshotWidgetPublisher(
    private val widgetManager: WidgetManager,
    private val jsonFileManager: JsonFileManager,
    private val posterDownloader: PosterDownloader,
    private val dateTimeProvider: DateTimeProvider,
) : WidgetPublisher {

    override suspend fun hasInstalledWidgets(): Boolean = suspendCoroutine { continuation ->
        widgetManager.hasInstalledWidgets { continuation.resume(it) }
    }

    override suspend fun publish(shows: List<WidgetShow>, theme: AppTheme) {
        val directoryPath = widgetManager.containerPath() ?: return
        val postersPath = "$directoryPath/$POSTERS_FOLDER_NAME"

        val entries = shows.map { show -> show.toEntry(postersPath) }
        posterDownloader.deleteExcept(postersPath, entries.mapNotNull { it.posterFileName }.toSet())

        jsonFileManager.writeToFile(
            directoryPath = directoryPath,
            fileName = SNAPSHOT_FILE_NAME,
            value = WidgetSnapshot(
                writtenAtMillis = dateTimeProvider.nowMillis(),
                themeName = theme.name,
                entries = entries,
            ),
            type = WidgetSnapshot::class,
        )

        widgetManager.reloadTimelines()
    }

    private suspend fun WidgetShow.toEntry(postersPath: String): WidgetSnapshotEntry = WidgetSnapshotEntry(
        tmdbId = tmdbId,
        showName = showName,
        episodeName = episodeName,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        posterFileName = downloadPoster(postersPath),
    )

    private suspend fun WidgetShow.downloadPoster(postersPath: String): String? {
        val path = posterUrl ?: return null
        val fileName = "$tmdbId-$POSTER_SIZE.jpg"
        val downloaded = posterDownloader.download(
            url = posterUrlFor(path),
            directoryPath = postersPath,
            fileName = fileName,
        )
        return if (downloaded) fileName else null
    }

    private fun posterUrlFor(path: String): String = when {
        path.startsWith("http") -> path.replace(POSTER_SIZE_SEGMENT, "/t/p/$POSTER_SIZE/")
        else -> "$POSTER_BASE_URL$POSTER_SIZE$path"
    }

    public companion object {
        public const val SNAPSHOT_FILE_NAME: String = "widget-snapshot.json"
        public const val POSTERS_FOLDER_NAME: String = "widget-posters"

        private const val POSTER_SIZE = "w185"
        private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/"
        private val POSTER_SIZE_SEGMENT = Regex("/t/p/(w\\d+|original)/")
    }
}
