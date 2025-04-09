package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger as KermitLogger
import com.thomaskioko.tvmaniac.core.networkutil.model.HttpExceptions
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json

const val TIMEOUT_DURATION: Long = 60_000

fun traktHttpClient(
  isDebug: Boolean = false,
  traktClientId: String,
  json: TraktJson,
  httpClientEngine: TraktHttpClientEngine,
  kermitLogger: KermitLogger,
) =
  HttpClient(httpClientEngine) {
    expectSuccess = true

    install(ContentNegotiation) { json(json = json) }

    install(DefaultRequest) {
      apply {
        url {
          protocol = URLProtocol.HTTPS
          host = "api.trakt.tv"
        }

        headers {
          append(HttpHeaders.ContentType, "application/json")
          append("trakt-api-version", "2")
          append("trakt-api-key", traktClientId)
        }
      }
    }

    HttpResponseValidator {
      validateResponse { response ->
        if (!response.status.isSuccess()) {
          val failureReason =
            when (response.status) {
              HttpStatusCode.Unauthorized -> "Unauthorized request"
              HttpStatusCode.Forbidden -> "${response.status.value} Missing API key."
              HttpStatusCode.NotFound -> "Invalid Request"
              HttpStatusCode.UpgradeRequired -> "Upgrade to VIP"
              HttpStatusCode.RequestTimeout -> "Network Timeout"
              in HttpStatusCode.InternalServerError..HttpStatusCode.GatewayTimeout ->
                "${response.status.value} Server Error"
              else -> "Network error!"
            }

          throw HttpExceptions(
            response = response,
            failureReason = failureReason,
            cachedResponseText = response.bodyAsText(),
          )
        }
      }
    }

    install(HttpTimeout) {
      requestTimeoutMillis = TIMEOUT_DURATION
      connectTimeoutMillis = TIMEOUT_DURATION
      socketTimeoutMillis = TIMEOUT_DURATION
    }

    install(Logging) {
      level = LogLevel.INFO
      logger = if (isDebug) {
          object : Logger {
            override fun log(message: String) {
              kermitLogger.info("TraktHttp", message)
            }
          }
        } else {
          Logger.EMPTY
        }
    }
  }
