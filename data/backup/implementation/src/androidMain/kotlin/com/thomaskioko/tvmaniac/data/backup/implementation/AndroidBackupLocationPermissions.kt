package com.thomaskioko.tvmaniac.data.backup.implementation

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.core.base.ApplicationContext
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.data.backup.api.BackupLocationPermissions
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidBackupLocationPermissions(
    @ApplicationContext private val context: Context,
    private val logger: Logger,
) : BackupLocationPermissions {

    override fun persist(location: String): Boolean {
        val uri = location.toUri()
        if (uri.scheme != CONTENT_SCHEME) return true

        return try {
            context.contentResolver.takePersistableUriPermission(uri, WRITE_FLAGS)
            true
        } catch (security: SecurityException) {
            logger.warning(TAG, "Cannot keep write access to $location: ${security.message}")
            false
        }
    }

    private companion object {
        private const val TAG = "BackupLocationPermissions"
        private const val CONTENT_SCHEME = "content"
        private const val WRITE_FLAGS = Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    }
}
