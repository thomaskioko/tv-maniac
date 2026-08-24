package com.thomaskioko.tvmaniac.core.filestore.implementation

import com.thomaskioko.tvmaniac.core.filestore.api.FileStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringByAppendingPathComponent
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.writeToFile

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@OptIn(ExperimentalForeignApi::class)
public class IosFileStore : FileStore {

    override fun writeText(directoryPath: String, fileName: String, contents: String) {
        NSFileManager.defaultManager.createDirectoryAtPath(
            path = directoryPath,
            withIntermediateDirectories = true,
            attributes = null,
            error = null,
        )
        (contents as NSString).writeToFile(
            path = pathFor(directoryPath, fileName),
            atomically = true,
            encoding = NSUTF8StringEncoding,
            error = null,
        )
    }

    override fun readText(directoryPath: String, fileName: String): String? =
        NSString.stringWithContentsOfFile(
            path = pathFor(directoryPath, fileName),
            encoding = NSUTF8StringEncoding,
            error = null,
        )

    override fun delete(directoryPath: String, fileName: String) {
        NSFileManager.defaultManager.removeItemAtPath(pathFor(directoryPath, fileName), error = null)
    }

    override fun exists(directoryPath: String, fileName: String): Boolean =
        NSFileManager.defaultManager.fileExistsAtPath(pathFor(directoryPath, fileName))

    private fun pathFor(directoryPath: String, fileName: String): String =
        (directoryPath as NSString).stringByAppendingPathComponent(fileName)
}
