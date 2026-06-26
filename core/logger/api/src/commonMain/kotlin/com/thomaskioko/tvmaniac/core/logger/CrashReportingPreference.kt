package com.thomaskioko.tvmaniac.core.logger

import kotlinx.coroutines.flow.Flow

public interface CrashReportingPreference {
    public fun observeCrashReportingEnabled(): Flow<Boolean>
}
