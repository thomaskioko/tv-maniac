package com.thomaskioko.tvmaniac.data.backup.implementation

import com.thomaskioko.tvmaniac.data.backup.api.BackupDestination
import com.thomaskioko.tvmaniac.data.backup.api.BackupDestinationBuilder
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
public class IosBackupDestinationBuilder : BackupDestinationBuilder {

    override fun build(location: String): BackupDestination = FileBackupDestination(location)

    override fun safetyCopy(): BackupDestination {
        val documents = NSSearchPathForDirectoriesInDomains(
            directory = NSDocumentDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true,
        ).first() as NSString
        return FileBackupDestination(documents.stringByAppendingPathComponent(BackupFormat.SAFETY_COPY_NAME))
    }
}

@OptIn(ExperimentalForeignApi::class)
internal class FileBackupDestination(private val path: String) : BackupDestination {

    override fun write(contents: String) {
        val written = (contents as NSString).writeToFile(
            path = path,
            atomically = true,
            encoding = NSUTF8StringEncoding,
            error = null,
        )
        if (!written) throw BackupLocationUnreadableException(path)
    }

    override fun read(): String = NSString.stringWithContentsOfFile(
        path = path,
        encoding = NSUTF8StringEncoding,
        error = null,
    ) ?: throw BackupLocationUnreadableException(path)
}
