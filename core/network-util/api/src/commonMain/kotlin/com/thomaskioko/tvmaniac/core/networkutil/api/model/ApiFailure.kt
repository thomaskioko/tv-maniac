package com.thomaskioko.tvmaniac.core.networkutil.api.model

public data class ApiFailure(
    public val provider: String,
    public val method: String,
    public val endpointTemplate: String,
    public val status: Int?,
    public val kind: ApiFailureKind,
    public val body: String?,
)

public enum class ApiFailureKind {
    Expected,
    Unexpected,
}

public fun ApiFailure.toLogMessage(): String =
    if (status != null) "HTTP $status $method $endpointTemplate" else "$method $endpointTemplate failed"

internal fun httpStatusToKind(status: Int): ApiFailureKind = when (status) {
    401, 404, 408, 420, 429 -> ApiFailureKind.Expected
    else -> ApiFailureKind.Unexpected
}

internal fun String.toEndpointTemplate(): String =
    substringBefore('?')
        .split('/')
        .joinToString("/") { segment -> if (segment.isNotEmpty() && segment.all(Char::isDigit)) "{id}" else segment }
