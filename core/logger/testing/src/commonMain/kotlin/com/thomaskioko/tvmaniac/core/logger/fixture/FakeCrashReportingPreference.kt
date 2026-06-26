package com.thomaskioko.tvmaniac.core.logger.fixture

import com.thomaskioko.tvmaniac.core.logger.CrashReportingPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeCrashReportingPreference : CrashReportingPreference {
    private val enabledFlow = MutableStateFlow(true)

    public fun setCrashReportingEnabled(enabled: Boolean) {
        enabledFlow.value = enabled
    }

    override fun observeCrashReportingEnabled(): Flow<Boolean> = enabledFlow.asStateFlow()
}
