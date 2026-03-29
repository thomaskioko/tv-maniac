package com.thomaskioko.tvmaniac.core.networkutil.api.model

public sealed class ApiResponse<out T> {
    public data class Success<T>(val body: T) : ApiResponse<T>()
    public data object Unauthenticated : ApiResponse<Nothing>()

    public sealed class Error<E> : ApiResponse<E>() {
        public data class HttpError<E>(
            val code: Int,
            val errorBody: String?,
            val errorMessage: String?,
        ) : Error<E>()

        public data class SerializationError(
            val message: String?,
            val errorMessage: String?,
        ) : Error<Nothing>()

        public data class GenericError(
            val message: String?,
            val errorMessage: String?,
        ) : Error<Nothing>()
    }
}

public fun <T> ApiResponse<T>.getOrThrow(): T = when (this) {
    is ApiResponse.Success -> body
    is ApiResponse.Unauthenticated -> throw AuthenticationException("Not authenticated")
    is ApiResponse.Error.HttpError -> throw ApiHttpException(code, "HTTP $code: $errorMessage")
    is ApiResponse.Error.SerializationError -> throw ApiSerializationException("Serialization error: $message")
    is ApiResponse.Error.GenericError -> throw ApiGenericException("Error: $message")
}

public fun <T> ApiResponse<T>.getOrNull(): T? = when (this) {
    is ApiResponse.Success -> body
    is ApiResponse.Unauthenticated -> null
    is ApiResponse.Error -> null
}
