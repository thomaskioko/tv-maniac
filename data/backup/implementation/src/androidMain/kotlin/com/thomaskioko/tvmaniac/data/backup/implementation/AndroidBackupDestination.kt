package com.thomaskioko.tvmaniac.data.backup.implementation

import android.content.Context
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.core.base.ApplicationContext
import com.thomaskioko.tvmaniac.data.backup.api.BackupDestination
import com.thomaskioko.tvmaniac.data.backup.api.BackupFormat
import com.thomaskioko.tvmaniac.data.backup.api.BackupLocationUnreadableException
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import java.io.File

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidBackupDestination(
    @ApplicationContext private val context: Context,
) : BackupDestination {

    override fun write(location: String, contents: String) {
        if (!isContentUri(location)) {
            File(location).writeText(contents)
            return
        }
        val stream = context.contentResolver.openOutputStream(location.toUri(), "wt")
            ?: throw BackupLocationUnreadableException(location)
        stream.use { it.write(contents.toByteArray()) }
    }

    override fun read(location: String): String {
        if (!isContentUri(location)) {
            val file = File(location)
            if (!file.exists()) throw BackupLocationUnreadableException(location)
            return file.readText()
        }
        val stream = context.contentResolver.openInputStream(location.toUri())
            ?: throw BackupLocationUnreadableException(location)
        return stream.use { it.readBytes().decodeToString() }
    }

    override fun safetyCopyLocation(): String = File(context.filesDir, BackupFormat.SAFETY_COPY_NAME).path

    private fun isContentUri(location: String): Boolean = location.toUri().scheme == "content"
}
