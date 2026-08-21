package com.thomaskioko.tvmaniac.data.backup.implementation

import com.thomaskioko.tvmaniac.data.backup.api.BackupLocationPermissions
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSURL

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class IosBackupLocationPermissions : BackupLocationPermissions {
    override fun persist(location: String): Boolean = true

    override fun displayName(location: String): String =
        NSURL.URLWithString(location)?.lastPathComponent ?: location
}
