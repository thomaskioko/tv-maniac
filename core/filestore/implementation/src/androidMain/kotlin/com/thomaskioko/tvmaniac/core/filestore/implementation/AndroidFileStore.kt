package com.thomaskioko.tvmaniac.core.filestore.implementation

import com.thomaskioko.tvmaniac.core.filestore.api.FileStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import java.io.File

@ContributesBinding(AppScope::class)
public class AndroidFileStore : FileStore {

    override fun writeText(directoryPath: String, fileName: String, contents: String) {
        val directory = File(directoryPath)
        directory.mkdirs()
        File(directory, fileName).writeText(contents)
    }

    override fun readText(directoryPath: String, fileName: String): String? {
        val file = File(directoryPath, fileName)
        return if (file.exists()) file.readText() else null
    }

    override fun delete(directoryPath: String, fileName: String) {
        File(directoryPath, fileName).delete()
    }

    override fun exists(directoryPath: String, fileName: String): Boolean =
        File(directoryPath, fileName).exists()
}
