package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.base.interactor.ResultInteractor
import dev.zacsweers.metro.Inject

@Inject
public class BackupNowInteractor(
    private val runAutoBackupInteractor: RunAutoBackupInteractor,
) : ResultInteractor<Unit, AutoBackupResult>() {

    override suspend fun doWork(params: Unit): AutoBackupResult =
        runAutoBackupInteractor.executeSync(Unit)
}
