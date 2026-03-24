package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.HttpExceptions
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.util.AttributeKey
import kotlinx.serialization.SerializationException

public val RequiresAuth: AttributeKey<Boolean> = AttributeKey("RequiresAuth")
public val IsAuthenticated: AttributeKey<() -> Boolean> = AttributeKey("IsAuthenticated")

public suspend inline fun <reified T> HttpClient.safeRequest(
    block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T> =
    try {
        val response = request { block() }
        ApiResponse.Success(response.body())
    } catch (e: AuthenticationException) {
        ApiResponse.Unauthenticated
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

public suspend inline fun <reified T> HttpClient.authSafeRequest(
    block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T> {
    val isAuthenticated = attributes.getOrNull(IsAuthenticated)
        ?: return ApiResponse.Unauthenticated
    if (!isAuthenticated()) return ApiResponse.Unauthenticated
    return safeRequest {
        attributes.put(RequiresAuth, true)
        block()
    }
}
