package com.thomaskioko.tvmaniac.data.backup.api

public interface BackupLocationPermissions {
    /**
     * @return true when the location can still be written to on a later launch.
     */
    public fun persist(location: String): Boolean

    /**
     * @return the file name a reader would recognise, falling back to the location itself.
     */
    public fun displayName(location: String): String
}
