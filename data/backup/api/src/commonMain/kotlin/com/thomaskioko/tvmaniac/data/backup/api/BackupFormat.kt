package com.thomaskioko.tvmaniac.data.backup.api

public object BackupFormat {
    public const val VERSION: Int = 1
    public const val FILE_PREFIX: String = "tvmaniac-backup-"
    public const val FILE_EXTENSION: String = ".json"
    public const val MIME_TYPE: String = "application/json"
    public const val SAFETY_COPY_NAME: String = "tvmaniac-pre-restore.json"
    public const val AUTO_BACKUP_NAME: String = "tvmaniac-backup.json"
}
