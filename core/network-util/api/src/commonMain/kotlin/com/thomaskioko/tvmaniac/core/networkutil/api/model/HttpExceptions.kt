package com.thomaskioko.tvmaniac.core.networkutil.api.model

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse

public class HttpExceptions(
    response: HttpResponse,
    failureReason: String?,
    cachedResponseText: String,
) : ResponseException(response, cachedResponseText) {
    override val message: String = "Status: ${response.status}" + " Failure: $failureReason"
}
