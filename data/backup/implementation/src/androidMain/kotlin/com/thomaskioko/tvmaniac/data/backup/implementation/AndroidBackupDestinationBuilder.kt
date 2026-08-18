package com.thomaskioko.tvmaniac.data.backup.implementation

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.core.base.ApplicationContext
import com.thomaskioko.tvmaniac.data.backup.api.BackupDestination
import com.thomaskioko.tvmaniac.data.backup.api.BackupDestinationBuilder
import com.thomaskioko.tvmaniac.data.backup.api.BackupFormat
import com.thomaskioko.tvmaniac.data.backup.api.BackupLocationUnreadableException
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import java.io.File

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidBackupDestinationBuilder(
    @ApplicationContext private val context: Context,
) : BackupDestinationBuilder {

    override fun build(location: String): BackupDestination =
        UriBackupDestination(context.contentResolver, location.toUri())

    override fun safetyCopy(): BackupDestination =
        FileBackupDestination(File(context.filesDir, BackupFormat.SAFETY_COPY_NAME))
}

internal class UriBackupDestination(
    private val contentResolver: ContentResolver,
    private val uri: Uri,
) : BackupDestination {

    override fun write(contents: String) {
        val stream = contentResolver.openOutputStream(uri, "wt")
            ?: throw BackupLocationUnreadableException(uri.toString())
        stream.use { it.write(contents.toByteArray()) }
    }

    override fun read(): String {
        val stream = contentResolver.openInputStream(uri)
            ?: throw BackupLocationUnreadableException(uri.toString())
        return stream.use { it.readBytes().decodeToString() }
    }
}

internal class FileBackupDestination(private val file: File) : BackupDestination {

    override fun write(contents: String) {
        file.parentFile?.mkdirs()
        file.writeText(contents)
    }

    override fun read(): String {
        if (!file.exists()) throw BackupLocationUnreadableException(file.path)
        return file.readText()
    }
}
