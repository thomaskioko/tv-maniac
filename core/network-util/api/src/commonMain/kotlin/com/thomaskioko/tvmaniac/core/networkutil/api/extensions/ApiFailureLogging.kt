package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.logger.CrashReportKeys
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiFailure
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiFailureKind
import com.thomaskioko.tvmaniac.core.networkutil.api.model.toLogMessage

public fun Logger.reportApiFailure(tag: String, failure: ApiFailure, throwable: Throwable) {
    val keys = mapOf(
        CrashReportKeys.PROVIDER to failure.provider,
        CrashReportKeys.METHOD to failure.method,
        CrashReportKeys.ENDPOINT to failure.endpointTemplate,
        CrashReportKeys.STATUS to (failure.status?.toString() ?: ""),
    )
    val message = failure.toLogMessage()
    when (failure.kind) {
        ApiFailureKind.Unexpected -> error(tag, message, throwable, keys)
        ApiFailureKind.Expected -> warning(tag, message, throwable, keys)
    }
}
