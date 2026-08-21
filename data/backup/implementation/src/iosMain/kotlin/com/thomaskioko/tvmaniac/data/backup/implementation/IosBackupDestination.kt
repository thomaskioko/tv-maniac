package com.thomaskioko.tvmaniac.data.backup.implementation

import com.thomaskioko.tvmaniac.data.backup.api.BackupDestination
import com.thomaskioko.tvmaniac.data.backup.api.BackupLocationUnreadableException
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLBookmarkResolutionWithSecurityScope
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.stringByAppendingPathComponent
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.writeToFile

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@OptIn(ExperimentalForeignApi::class)
public class IosBackupDestination : BackupDestination {

    override fun write(folder: String, fileName: String, contents: String): String {
        val resolved = resolveFolder(folder)
        val accessing = resolved.url?.startAccessingSecurityScopedResource() ?: false
        try {
            val location = (resolved.path as NSString).stringByAppendingPathComponent(fileName)
            val written = (contents as NSString).writeToFile(
                path = location,
                atomically = true,
                encoding = NSUTF8StringEncoding,
                error = null,
            )
            if (!written) throw BackupLocationUnreadableException(location)
            return location
        } finally {
            if (accessing) resolved.url?.stopAccessingSecurityScopedResource()
        }
    }

    override fun read(location: String): String = NSString.stringWithContentsOfFile(
        path = location,
        encoding = NSUTF8StringEncoding,
        error = null,
    ) ?: throw BackupLocationUnreadableException(location)

    override fun safetyCopyFolder(): String = documentsPath()

    override fun defaultBackupFolder(): String = documentsPath()

    private fun resolveFolder(folder: String): ResolvedFolder {
        if (!folder.startsWith(BOOKMARK_PREFIX)) return ResolvedFolder(path = folder, url = null)

        val data = NSData.create(
            base64EncodedString = folder.removePrefix(BOOKMARK_PREFIX),
            options = 0u,
        ) ?: throw BackupLocationUnreadableException(folder)

        val url = NSURL.URLByResolvingBookmarkData(
            bookmarkData = data,
            options = NSURLBookmarkResolutionWithSecurityScope,
            relativeToURL = null,
            bookmarkDataIsStale = null,
            error = null,
        ) ?: throw BackupLocationUnreadableException(folder)

        val path = url.path ?: throw BackupLocationUnreadableException(folder)
        return ResolvedFolder(path = path, url = url)
    }

    private fun documentsPath(): String = NSSearchPathForDirectoriesInDomains(
        directory = NSDocumentDirectory,
        domainMask = NSUserDomainMask,
        expandTilde = true,
    ).first() as String

    private data class ResolvedFolder(
        val path: String,
        val url: NSURL?,
    )

    public companion object {
        public const val BOOKMARK_PREFIX: String = "bookmark:"

        public fun bookmarkLocation(base64: String): String = "$BOOKMARK_PREFIX$base64"
    }
}
