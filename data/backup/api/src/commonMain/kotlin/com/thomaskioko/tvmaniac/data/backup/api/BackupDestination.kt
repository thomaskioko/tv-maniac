package com.thomaskioko.tvmaniac.data.backup.api

public interface BackupDestination {
    /**
     * Writes [contents] into [folder] under [fileName], replacing whatever was there before.
     *
     * @return where the contents landed, so the caller can read them back.
     */
    public fun write(folder: String, fileName: String, contents: String): String

    public fun read(location: String): String

    /**
     * @return the app's own folder, used for the copy taken before a restore.
     */
    public fun safetyCopyFolder(): String

    /**
     * @return the folder backups go to when the platform has one the user can reach, or null when
     * the user has to choose.
     */
    public fun defaultBackupFolder(): String?
}
