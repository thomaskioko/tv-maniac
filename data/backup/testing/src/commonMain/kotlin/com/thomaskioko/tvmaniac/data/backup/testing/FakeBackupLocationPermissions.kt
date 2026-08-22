package com.thomaskioko.tvmaniac.data.backup.testing

import com.thomaskioko.tvmaniac.data.backup.api.BackupLocationPermissions

public class FakeBackupLocationPermissions : BackupLocationPermissions {

    private var persisted = true
    private val requested = mutableListOf<String>()

    public fun setPersisted(value: Boolean) {
        persisted = value
    }

    public fun requested(): List<String> = requested

    override fun persist(location: String): Boolean {
        requested.add(location)
        return persisted
    }
}
