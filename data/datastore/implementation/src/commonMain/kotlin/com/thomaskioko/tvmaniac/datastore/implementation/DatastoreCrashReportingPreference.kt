package com.thomaskioko.tvmaniac.datastore.implementation

import com.thomaskioko.tvmaniac.core.logger.CrashReportingPreference
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DatastoreCrashReportingPreference(
    private val datastoreRepository: DatastoreRepository,
) : CrashReportingPreference {
    override fun observeCrashReportingEnabled(): Flow<Boolean> =
        datastoreRepository.observeCrashReportingEnabled()
}
