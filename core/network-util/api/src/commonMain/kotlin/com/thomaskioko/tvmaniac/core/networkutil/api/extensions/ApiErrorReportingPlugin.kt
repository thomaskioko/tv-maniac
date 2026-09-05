package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiFailure
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiFailureKind
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiHttpException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.NoInternetException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.httpStatusToKind
import com.thomaskioko.tvmaniac.core.networkutil.api.model.toEndpointTemplate
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.HttpRequestTimeoutException
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
    public var provider: String = ""
    public var onFailure: (failure: ApiFailure, throwable: Throwable) -> Unit = { _, _ -> }
}

public val ApiErrorReportingPlugin: ClientPlugin<ApiErrorReportingPluginConfig> = createClientPlugin(
    "ApiErrorReportingPlugin",
    ::ApiErrorReportingPluginConfig,
) {
    val provider = pluginConfig.provider
    val onFailure = pluginConfig.onFailure

    on(Send) { request ->
        val call = try {
            proceed(request)
        } catch (e: CancellationException) {
            throw e
        } catch (e: NoInternetException) {
            onFailure(request.toApiFailure(provider, status = null, kind = ApiFailureKind.Expected, body = null), e)
            throw e
        } catch (e: AuthenticationException) {
            onFailure(request.toApiFailure(provider, status = null, kind = ApiFailureKind.Expected, body = null), e)
            throw e
        } catch (e: HttpRequestTimeoutException) {
            onFailure(request.toApiFailure(provider, status = null, kind = ApiFailureKind.Expected, body = null), e)
            throw e
        } catch (e: Throwable) {
            val status = (e as? ResponseException)?.response?.status?.value
            val kind = status?.let(::httpStatusToKind) ?: ApiFailureKind.Unexpected
            val body = (e as? ResponseException)?.response?.errorBody()
            onFailure(request.toApiFailure(provider, status = status, kind = kind, body = body), e)
            throw e
        }

        val status = call.response.status
        if (!status.isSuccess()) {
            val body = call.response.errorBody()
            val message = withBody("HTTP ${status.value} ${request.requestLabel}", body)
            onFailure(
                request.toApiFailure(provider, status = status.value, kind = httpStatusToKind(status.value), body = body),
                ApiHttpException(status.value, message),
            )
        }
        call
    }

    client.responsePipeline.intercept(HttpResponsePipeline.Receive) {
        val alreadyReported = !context.response.status.isSuccess()
        try {
            proceed()
        } catch (e: CancellationException) {
            throw e
        } catch (e: ContentConvertException) {
            if (!alreadyReported) reportUnreadableBody(provider, onFailure, context, e)
            throw e
        } catch (e: SerializationException) {
            if (!alreadyReported) reportUnreadableBody(provider, onFailure, context, e)
            throw e
        }
    }
}

private suspend fun reportUnreadableBody(
    provider: String,
    onFailure: (ApiFailure, Throwable) -> Unit,
    call: HttpClientCall,
    cause: Throwable,
) {
    val body = call.response.errorBody()
    onFailure(call.request.toApiFailure(provider, status = null, kind = ApiFailureKind.Unexpected, body = body), cause)
}

private const val ERROR_BODY_LIMIT = 500

private val HttpRequestBuilder.requestLabel: String
    get() = "${method.value} ${url.host}${url.encodedPath}"

private val HttpRequest.requestLabel: String
    get() = "${method.value} ${url.host}${url.encodedPath}"

private fun HttpRequestBuilder.toApiFailure(provider: String, status: Int?, kind: ApiFailureKind, body: String?): ApiFailure =
    ApiFailure(
        provider = provider,
        method = method.value,
        endpointTemplate = url.encodedPath.toEndpointTemplate(),
        status = status,
        kind = kind,
        body = body,
    )

private fun HttpRequest.toApiFailure(provider: String, status: Int?, kind: ApiFailureKind, body: String?): ApiFailure =
    ApiFailure(
        provider = provider,
        method = method.value,
        endpointTemplate = url.encodedPath.toEndpointTemplate(),
        status = status,
        kind = kind,
        body = body,
    )

private suspend fun HttpResponse.errorBody(): String? =
    runCatching { bodyAsText() }.getOrNull()?.take(ERROR_BODY_LIMIT)?.takeIf { it.isNotBlank() }

private fun withBody(message: String, body: String?): String =
    if (body == null) message else "$message: $body"
