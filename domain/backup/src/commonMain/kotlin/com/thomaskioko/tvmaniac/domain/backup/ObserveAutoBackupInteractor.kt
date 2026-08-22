package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.backup.api.AutoBackupPreferences
import com.thomaskioko.tvmaniac.datastore.api.AutoBackupInterval
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Inject
public class ObserveAutoBackupInteractor(
    private val datastoreRepository: DatastoreRepository,
    private val autoBackupPreferences: AutoBackupPreferences,
    private val dateTimeProvider: DateTimeProvider,
) : SubjectInteractor<Unit, AutoBackupState>() {

    override fun createObservable(params: Unit): Flow<AutoBackupState> = combine(
        datastoreRepository.observeAutoBackupEnabled(),
        datastoreRepository.observeAutoBackupInterval(),
        datastoreRepository.observeBackupFolder(),
        autoBackupPreferences.observeStatus(),
    ) { enabled, interval, location, status ->
        AutoBackupState(
            enabled = enabled,
            interval = interval,
            location = location,
            lastRunDate = status.lastRunAt?.let { dateTimeProvider.epochToDisplayDateTime(it) },
            lastRunFailed = status.lastRunFailed,
        )
    }
}

public data class AutoBackupState(
    val enabled: Boolean = false,
    val interval: AutoBackupInterval = AutoBackupInterval.Default,
    val location: String? = null,
    val lastRunDate: String? = null,
    val lastRunFailed: Boolean = false,
)
