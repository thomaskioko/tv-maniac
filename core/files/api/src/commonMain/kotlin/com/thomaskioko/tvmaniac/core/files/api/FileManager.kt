package com.thomaskioko.tvmaniac.core.files.api

public interface FileManager {

    public fun writeToFile(directoryPath: String, fileName: String, contents: String)

    public fun getFileContent(directoryPath: String, fileName: String): String?

    public fun delete(directoryPath: String, fileName: String)

    public fun doesFileExist(directoryPath: String, fileName: String): Boolean
}
