package com.thomaskioko.tvmaniac.data.backup.implementation

import com.thomaskioko.tvmaniac.data.backup.api.BackupDestination
import com.thomaskioko.tvmaniac.data.backup.api.BackupFormat
import com.thomaskioko.tvmaniac.data.backup.api.BackupLocationUnreadableException
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringByAppendingPathComponent
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.writeToFile

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@OptIn(ExperimentalForeignApi::class)
public class IosBackupDestination : BackupDestination {

    override fun write(location: String, contents: String) {
        val written = (contents as NSString).writeToFile(
            path = location,
            atomically = true,
            encoding = NSUTF8StringEncoding,
            error = null,
        )
        if (!written) throw BackupLocationUnreadableException(location)
    }

    override fun read(location: String): String = NSString.stringWithContentsOfFile(
        path = location,
        encoding = NSUTF8StringEncoding,
        error = null,
    ) ?: throw BackupLocationUnreadableException(location)

    override fun safetyCopyLocation(): String {
        val documents = NSSearchPathForDirectoriesInDomains(
            directory = NSDocumentDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true,
        ).first() as NSString
        return documents.stringByAppendingPathComponent(BackupFormat.SAFETY_COPY_NAME)
    }
}
