package com.thomaskioko.tvmaniac.core.networkutil

import com.thomaskioko.tvmaniac.util.ExceptionHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import kotlinx.serialization.SerializationException


suspend inline fun <reified T, reified E> HttpClient.safeRequest(
    exceptionHandler: ExceptionHandler,
    block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T, E> =
    try {
        val response = request { block() }
        ApiResponse.Success(response.body())
    } catch (e: ClientRequestException) {
        ApiResponse.Error.HttpError(e.response.status.value, e.errorBody())
    } catch (e: ServerResponseException) {
        ApiResponse.Error.HttpError(e.response.status.value, e.errorBody())
    } catch (e: SerializationException) {
        ApiResponse.Error.SerializationError(exceptionHandler.resolveError(e))
    } catch (e: Exception) {
        ApiResponse.Error.GenericError(exceptionHandler.resolveError(e))
    }

suspend inline fun <reified E> ResponseException.errorBody(): E? =
    try {
        response.body()
    } catch (e: SerializationException) {
        null
    }


sealed class ApiResponse<out T, out E> {
    /**
     * Represents successful network responses (2xx).
     */
    data class Success<T>(val body: T) : ApiResponse<T, Nothing>()

    sealed class Error<E> : ApiResponse<Nothing, E>() {
        /**
         * Represents server (50x) and client (40x) errors.
         */
        data class HttpError<E>(val code: Int, val errorBody: E?) : Error<E>()

        /**
         * Represent SerializationExceptions.
         */
        data class SerializationError(val errorMessage: String?) : Error<Nothing>()

        /**
         * Represent other exceptions.
         */
        data class GenericError(val errorMessage: String?) : Error<Nothing>()

    }
}
