package com.thomaskioko.tvmaniac.domain.widget

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import okio.FileSystem
import okio.Path.Companion.toPath

public interface WidgetSnapshotWriter {
    public fun write(directoryPath: String, snapshot: WidgetSnapshot)
}

@ContributesBinding(AppScope::class)
public class DefaultWidgetSnapshotWriter : WidgetSnapshotWriter {

    override fun write(directoryPath: String, snapshot: WidgetSnapshot) {
        val directory = directoryPath.toPath()
        FileSystem.SYSTEM.createDirectories(directory)
        FileSystem.SYSTEM.write(directory / SNAPSHOT_FILE_NAME) {
            writeUtf8(WidgetSnapshotJson.encode(snapshot))
        }
    }

    public companion object {
        public const val SNAPSHOT_FILE_NAME: String = "widget-snapshot.json"
    }
}
