package com.thomaskioko.tvmaniac.core.networkutil.model

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.SerializationException

public suspend inline fun <reified T> HttpClient.safeRequest(
    block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T> =
    try {
        val response = request { block() }
        ApiResponse.Success(response.body())
    } catch (exception: ClientRequestException) {
        ApiResponse.Error.HttpError(
            code = exception.response.status.value,
            errorBody = exception.response.body(),
            errorMessage = "Status Code: ${exception.response.status.value} - API Key Missing",
        )
    } catch (exception: HttpExceptions) {
        ApiResponse.Error.HttpError(
            code = exception.response.status.value,
            errorBody = exception.response.body(),
            errorMessage = exception.message,
        )
    } catch (e: SerializationException) {
        ApiResponse.Error.SerializationError(
            message = e.message,
            errorMessage = "Something went wrong",
        )
    } catch (e: Exception) {
        ApiResponse.Error.GenericError(
            message = e.message,
            errorMessage = "Something went wrong",
        )
    }

public sealed class ApiResponse<out T> {
    /** Represents successful network responses (2xx). */
    public data class Success<T>(val body: T) : ApiResponse<T>()

    public sealed class Error<E> : ApiResponse<E>() {
        /**
         * Represents server errors.
         *
         * @param code HTTP Status code
         * @param errorBody Response body
         * @param errorMessage Custom error message
         */
        public data class HttpError<E>(
            val code: Int,
            val errorBody: String?,
            val errorMessage: String?,
        ) : Error<E>()

        /**
         * Represent SerializationExceptions.
         *
         * @param message Detail exception message
         * @param errorMessage Formatted error message
         */
        public data class SerializationError(
            val message: String?,
            val errorMessage: String?,
        ) : Error<Nothing>()

        /**
         * Represent other exceptions.
         *
         * @param message Detail exception message
         * @param errorMessage Formatted error message
         */
        public data class GenericError(
            val message: String?,
            val errorMessage: String?,
        ) : Error<Nothing>()
    }
}

public class HttpExceptions(
    response: HttpResponse,
    failureReason: String?,
    cachedResponseText: String,
) : ResponseException(response, cachedResponseText) {
    override val message: String = "Status: ${response.status}" + " Failure: $failureReason"
}

public fun <T> ApiResponse<T>.getOrThrow(): T = when (this) {
    is ApiResponse.Success -> body
    is ApiResponse.Error.HttpError -> throw Throwable("HTTP $code: $errorMessage")
    is ApiResponse.Error.SerializationError -> throw Throwable("Serialization error: $message")
    is ApiResponse.Error.GenericError -> throw Throwable("Error: $message")
}

public fun <T> ApiResponse<T>.getOrNull(): T? = when (this) {
    is ApiResponse.Success -> body
    is ApiResponse.Error -> null
}
