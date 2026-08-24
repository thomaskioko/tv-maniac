package com.thomaskioko.tvmaniac.core.files.implementation

import com.thomaskioko.tvmaniac.core.files.api.FileManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import java.io.File

@ContributesBinding(AppScope::class)
public class AndroidFileManager : FileManager {

    override fun writeToFile(directoryPath: String, fileName: String, contents: String) {
        val directory = File(directoryPath)
        directory.mkdirs()
        File(directory, fileName).writeText(contents)
    }

    override fun getFileContent(directoryPath: String, fileName: String): String? {
        val file = File(directoryPath, fileName)
        return if (file.exists()) file.readText() else null
    }

    override fun delete(directoryPath: String, fileName: String) {
        File(directoryPath, fileName).delete()
    }

    override fun doesFileExist(directoryPath: String, fileName: String): Boolean =
        File(directoryPath, fileName).exists()
}
