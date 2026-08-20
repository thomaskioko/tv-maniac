package com.thomaskioko.tvmaniac.data.backup.testing

import com.thomaskioko.tvmaniac.data.backup.api.BackupList
import com.thomaskioko.tvmaniac.data.backup.api.RestoredListWriter

public class FakeRestoredListWriter : RestoredListWriter {

    private var restoredCount: Int? = null
    private val received = mutableListOf<List<BackupList>>()

    public fun setRestoredCount(count: Int) {
        restoredCount = count
    }

    public fun received(): List<List<BackupList>> = received

    override suspend fun restoreLists(lists: List<BackupList>): Int {
        received.add(lists)
        return restoredCount ?: lists.size
    }
}
