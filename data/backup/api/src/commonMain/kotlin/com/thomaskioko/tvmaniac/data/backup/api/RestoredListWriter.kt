package com.thomaskioko.tvmaniac.data.backup.api

import com.thomaskioko.tvmaniac.data.backup.api.model.BackupList

public interface RestoredListWriter {
    public suspend fun restoreLists(lists: List<BackupList>): Int
}
