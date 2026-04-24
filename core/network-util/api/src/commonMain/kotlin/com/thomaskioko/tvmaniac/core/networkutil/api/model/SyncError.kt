package com.thomaskioko.tvmaniac.core.networkutil.api.model

public sealed class SyncError {

    public abstract val message: String

    public sealed class Retryable : SyncError() {
        public data class RateLimited(
            override val message: String = "Too many requests. Please try again later.",
        ) : Retryable()

        public data class ServerError(
            val statusCode: Int,
            override val message: String = "Server error. Please try again later.",
        ) : Retryable()

        public data class NetworkError(
            override val message: String = "Network error. Please check your connection.",
        ) : Retryable()

        public data class Timeout(
            override val message: String = "Request timed out. Please try again.",
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
            override val message: String = "Failed to process data. Please try again.",
        ) : Permanent()

        public data class Forbidden(
            override val message: String = "Access forbidden.",
        ) : Permanent()
    }

    public data class Unknown(
        override val message: String = "Something went wrong. Please try again.",
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
            message = message ?: "Failed to process data. Please try again.",
        )
        is ApiResponse.Error.NetworkFailure -> kind.toSyncError()
        is ApiResponse.Error.OfflineError -> SyncError.Retryable.NetworkError(errorMessage)
    }
}

public fun Throwable.toSyncError(): SyncError? = when (this) {
    is SyncException -> syncError
    is ApiHttpException -> classifyHttpError(code, message)
    is ApiSerializationException -> SyncError.Permanent.InvalidData(message)
    is ApiNetworkException -> kind.toSyncError()
    is AuthenticationException -> SyncError.Permanent.AuthenticationFailed(message)
    is NoInternetException -> SyncError.Retryable.NetworkError("No internet connection")
    else -> (cause as? SyncException)?.syncError
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

private fun ApiResponse.Error.NetworkFailure.Kind.toSyncError(): SyncError = when (this) {
    ApiResponse.Error.NetworkFailure.Kind.Timeout -> SyncError.Retryable.Timeout()
    ApiResponse.Error.NetworkFailure.Kind.Connectivity -> SyncError.Retryable.NetworkError()
    ApiResponse.Error.NetworkFailure.Kind.Unknown -> SyncError.Unknown()
}
