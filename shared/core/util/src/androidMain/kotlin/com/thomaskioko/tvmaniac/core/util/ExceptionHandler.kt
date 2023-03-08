package com.thomaskioko.tvmaniac.core.util

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.JsonConvertException
import java.net.UnknownHostException

actual object ExceptionHandler : Exception() {

    override val message: String
        get() = cause?.message ?: "Something went wrong"

    actual fun Throwable.resolveError() = when (this) {
        is ClientRequestException ->
            when (response.status.value) {
                401 -> "Unauthorized request"
                403 -> "Invalid API key"
                404 -> "Invalid Request"
                420 -> "Account limit exceeded"
                426 -> "Upgrade to VIP"
                in 500..522 -> "${response.status.value} Server Error"
                else -> "Network error!"
            }

        is UnknownHostException -> "No Internet Connection!"
        is ServerResponseException -> "Internal server error"
        is JsonConvertException -> "Error Parsing Response"
        else -> getErrorMessage()
    }

    private fun Throwable.getErrorMessage(): String = message ?: "Something went wrong"

}
