package com.thomaskioko.tvmaniac.core.filestore.testing

import com.thomaskioko.tvmaniac.core.filestore.api.FileStore

public class FakeFileStore : FileStore {

    private val files = mutableMapOf<String, String>()

    public var failWith: Exception? = null

    public fun contentsOf(directoryPath: String, fileName: String): String? =
        files[keyOf(directoryPath, fileName)]

    override fun writeText(directoryPath: String, fileName: String, contents: String) {
        failWith?.let { throw it }
        files[keyOf(directoryPath, fileName)] = contents
    }

    override fun readText(directoryPath: String, fileName: String): String? =
        files[keyOf(directoryPath, fileName)]

    override fun delete(directoryPath: String, fileName: String) {
        files.remove(keyOf(directoryPath, fileName))
    }

    override fun exists(directoryPath: String, fileName: String): Boolean =
        files.containsKey(keyOf(directoryPath, fileName))

    private fun keyOf(directoryPath: String, fileName: String): String = "$directoryPath/$fileName"
}
