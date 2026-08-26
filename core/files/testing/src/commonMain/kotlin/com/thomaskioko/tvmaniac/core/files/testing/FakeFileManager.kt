package com.thomaskioko.tvmaniac.core.files.testing

import com.thomaskioko.tvmaniac.core.files.api.FileManager

public class FakeFileManager : FileManager {

    private val files = mutableMapOf<String, String>()

    public var failWith: Exception? = null

    public fun contentsOf(directoryPath: String, fileName: String): String? =
        files[keyOf(directoryPath, fileName)]

    override fun writeToFile(directoryPath: String, fileName: String, contents: String) {
        failWith?.let { throw it }
        files[keyOf(directoryPath, fileName)] = contents
    }

    override fun getFileContent(directoryPath: String, fileName: String): String? =
        files[keyOf(directoryPath, fileName)]

    override fun delete(directoryPath: String, fileName: String) {
        files.remove(keyOf(directoryPath, fileName))
    }

    override fun doesFileExist(directoryPath: String, fileName: String): Boolean =
        files.containsKey(keyOf(directoryPath, fileName))

    private fun keyOf(directoryPath: String, fileName: String): String = "$directoryPath/$fileName"
}
