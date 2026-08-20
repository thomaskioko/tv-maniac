package com.thomaskioko.tvmaniac.data.backup.api

public interface RestoredListWriter {
    public suspend fun restoreLists(lists: List<BackupList>): Int
}
