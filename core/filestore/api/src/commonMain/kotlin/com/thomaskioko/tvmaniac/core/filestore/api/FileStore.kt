package com.thomaskioko.tvmaniac.core.filestore.api

public interface FileStore {

    public fun writeText(directoryPath: String, fileName: String, contents: String)

    public fun readText(directoryPath: String, fileName: String): String?

    public fun delete(directoryPath: String, fileName: String)

    public fun exists(directoryPath: String, fileName: String): Boolean
}
