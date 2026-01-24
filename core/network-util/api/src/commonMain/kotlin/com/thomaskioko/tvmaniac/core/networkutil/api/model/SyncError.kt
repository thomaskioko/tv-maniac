package com.thomaskioko.tvmaniac.core.networkutil.api.model

public sealed class SyncError {

    public abstract val message: String

    public sealed class Retryable : SyncError() {
        public data class RateLimited(
            override val message: String = "Rate limited. Please try again later.",
        ) : Retryable()

        public data class ServerError(
            val statusCode: Int,
            override val message: String,
        ) : Retryable()

        public data class NetworkError(
            override val message: String,
        ) : Retryable()

        public data class Timeout(
            override val message: String = "Request timed out.",
        ) : Retryable()
    }

    public sealed class Permanent : SyncError() {
        public data class AuthenticationFailed(
            override val message: String = "Authentication failed. Please sign in again.",
        ) : Permanent()

        public data class NotFound(
            override val message: String = "Resource not found.",
        ) : Permanent()

        public data class InvalidData(
            override val message: String,
        ) : Permanent()

        public data class Forbidden(
            override val message: String = "Access forbidden.",
        ) : Permanent()
    }

    public data class Unknown(
        override val message: String,
    ) : SyncError()
}

public val SyncError.isRetryable: Boolean
    get() = this is SyncError.Retryable

public val SyncError.isPermanent: Boolean
    get() = this is SyncError.Permanent

public fun <T> ApiResponse.Error<T>.toSyncError(): SyncError {
    return when (this) {
        is ApiResponse.Error.HttpError -> classifyHttpError(code, errorMessage)
        is ApiResponse.Error.SerializationError -> SyncError.Permanent.InvalidData(
            message = message ?: "Failed to parse response data.",
        )
        is ApiResponse.Error.GenericError -> classifyGenericError(message)
    }
}

public fun classifyHttpError(code: Int, errorMessage: String?): SyncError {
    val message = errorMessage ?: "HTTP Error $code"
    return when (code) {
        401 -> SyncError.Permanent.AuthenticationFailed(message)
        403 -> SyncError.Permanent.Forbidden(message)
        404 -> SyncError.Permanent.NotFound(message)
        408 -> SyncError.Retryable.Timeout(message)
        429 -> SyncError.Retryable.RateLimited(message)
        in 500..599 -> SyncError.Retryable.ServerError(statusCode = code, message = message)
        else -> SyncError.Unknown(message)
    }
}

public fun classifyGenericError(message: String?): SyncError {
    val errorMessage = message ?: "Unknown error"
    val lowerMessage = errorMessage.lowercase()

    return when {
        lowerMessage.contains("timeout") -> SyncError.Retryable.Timeout(errorMessage)
        lowerMessage.contains("timed out") -> SyncError.Retryable.Timeout(errorMessage)
        lowerMessage.contains("network") -> SyncError.Retryable.NetworkError(errorMessage)
        lowerMessage.contains("connection") -> SyncError.Retryable.NetworkError(errorMessage)
        lowerMessage.contains("socket") -> SyncError.Retryable.NetworkError(errorMessage)
        lowerMessage.contains("unreachable") -> SyncError.Retryable.NetworkError(errorMessage)
        lowerMessage.contains("dns") -> SyncError.Retryable.NetworkError(errorMessage)
        lowerMessage.contains("no address") -> SyncError.Retryable.NetworkError(errorMessage)
        lowerMessage.contains("429") -> SyncError.Retryable.RateLimited(errorMessage)
        lowerMessage.contains("rate limit") -> SyncError.Retryable.RateLimited(errorMessage)
        else -> SyncError.Unknown(errorMessage)
    }
}
