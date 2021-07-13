package com.thomaskioko.tvmaniac.util

import io.ktor.client.features.*
import io.ktor.client.statement.*

open class ExceptionHandler(
    val errorMessage: String,
) : Exception() {

    override val message: String
        get() = cause.message ?: "Something went wrong"

    override val cause: Throwable
        get() = Throwable(errorMessage)

    companion object {
        fun parseException(response: HttpResponse): ExceptionHandler {
            return ExceptionHandler("Unexpected error. ResponseCode:: ${response.status.value}ً")
        }
    }
}

fun Throwable.resolveError() = when (this) {
    is HttpResponse -> {
        when (status.value) {
            400 -> ExceptionHandler.parseException(this)
            401 -> ExceptionHandler(errorMessage = "Authentication failed!")
            502 -> ExceptionHandler("Internal error!")
            else -> ExceptionHandler.parseException(this)
        }
    }
    is ServerResponseException -> ExceptionHandler(errorMessage = getErrorMessage())
    else -> ExceptionHandler(errorMessage = "Something went wrong")
}

fun ResponseException.getErrorMessage(): String {
    return if (isDebug) {
        "Server Error: ${response.status.value} /n $message"
    } else {
        "Connection to server failed."
    }
}
