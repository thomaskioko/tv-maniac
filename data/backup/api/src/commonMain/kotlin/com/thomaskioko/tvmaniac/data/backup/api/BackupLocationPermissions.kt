package com.thomaskioko.tvmaniac.data.backup.api

public interface BackupLocationPermissions {
    /**
     * @return true when the location can still be written to on a later launch.
     */
    public fun persist(location: String): Boolean
}
