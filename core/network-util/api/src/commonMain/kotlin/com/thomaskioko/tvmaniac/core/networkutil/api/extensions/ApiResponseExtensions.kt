package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.HttpExceptions
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerializationException

public suspend inline fun <reified T> HttpClient.safeRequest(
    block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T> =
    try {
        val response = request { block() }
        ApiResponse.Success(response.body())
    } catch (exception: ClientRequestException) {
        val errorBody: String = exception.response.bodyAsText()
        val url = exception.response.call.request.url
        ApiResponse.Error.HttpError(
            code = exception.response.status.value,
            errorBody = errorBody,
            errorMessage = "HTTP ${exception.response.status.value} from $url: $errorBody",
        )
    } catch (exception: HttpExceptions) {
        ApiResponse.Error.HttpError(
            code = exception.response.status.value,
            errorBody = exception.response.bodyAsText(),
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
