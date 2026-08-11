package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiHttpException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.NoInternetException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.ContentConvertException
import kotlinx.serialization.SerializationException
import kotlin.coroutines.cancellation.CancellationException

public class ApiErrorReportingPluginConfig {
    public var onError: (message: String, throwable: Throwable) -> Unit = { _, _ -> }
}

public val ApiErrorReportingPlugin: ClientPlugin<ApiErrorReportingPluginConfig> = createClientPlugin(
    "ApiErrorReportingPlugin",
    ::ApiErrorReportingPluginConfig,
) {
    val onError = pluginConfig.onError

    on(Send) { request ->
        val call = try {
            proceed(request)
        } catch (e: CancellationException) {
            throw e
        } catch (e: NoInternetException) {
            throw e
        } catch (e: AuthenticationException) {
            throw e
        } catch (e: Throwable) {
            val body = (e as? ResponseException)?.response?.errorBody()
            onError(withBody("${request.requestLabel} failed", body), e)
            throw e
        }

        val status = call.response.status
        if (!status.isSuccess()) {
            val message = withBody("HTTP ${status.value} ${request.requestLabel}", call.response.errorBody())
            onError(message, ApiHttpException(status.value, message))
        }
        call
    }

    client.responsePipeline.intercept(HttpResponsePipeline.Receive) {
        try {
            proceed()
        } catch (e: CancellationException) {
            throw e
        } catch (e: ContentConvertException) {
            onError(withBody("Deserialization failed for ${context.request.requestLabel}", context.response.errorBody()), e)
            throw e
        } catch (e: SerializationException) {
            onError(withBody("Deserialization failed for ${context.request.requestLabel}", context.response.errorBody()), e)
            throw e
        }
    }
}

private const val ERROR_BODY_LIMIT = 500

private val HttpRequestBuilder.requestLabel: String
    get() = "${method.value} ${url.host}${url.encodedPath}"

private val HttpRequest.requestLabel: String
    get() = "${method.value} ${url.host}${url.encodedPath}"

private suspend fun HttpResponse.errorBody(): String? =
    runCatching { bodyAsText() }.getOrNull()?.take(ERROR_BODY_LIMIT)?.takeIf { it.isNotBlank() }

private fun withBody(message: String, body: String?): String =
    if (body == null) message else "$message: $body"
