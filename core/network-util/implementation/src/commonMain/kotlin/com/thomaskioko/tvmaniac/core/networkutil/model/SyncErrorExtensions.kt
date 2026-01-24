package com.thomaskioko.tvmaniac.core.networkutil.model

import com.thomaskioko.tvmaniac.core.networkutil.api.model.HttpExceptions
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError
import com.thomaskioko.tvmaniac.core.networkutil.api.model.classifyGenericError
import com.thomaskioko.tvmaniac.core.networkutil.api.model.classifyHttpError
import io.ktor.client.plugins.ClientRequestException

public fun Throwable.toSyncError(): SyncError {
    return when (this) {
        is ClientRequestException ->
            classifyHttpError(response.status.value, message)
        is HttpExceptions ->
            classifyHttpError(response.status.value, message)
        else -> classifyGenericError(message)
    }
}
