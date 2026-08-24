package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.core.files.api.FileManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

public interface WidgetSnapshotWriter {
    public fun write(directoryPath: String, snapshot: WidgetSnapshot)
}

@ContributesBinding(AppScope::class)
public class DefaultWidgetSnapshotWriter(
    private val fileManager: FileManager,
) : WidgetSnapshotWriter {

    override fun write(directoryPath: String, snapshot: WidgetSnapshot) {
        fileManager.writeToFile(
            directoryPath = directoryPath,
            fileName = SNAPSHOT_FILE_NAME,
            contents = WidgetSnapshotJson.encode(snapshot),
        )
    }

    public companion object {
        public const val SNAPSHOT_FILE_NAME: String = "widget-snapshot.json"
    }
}
