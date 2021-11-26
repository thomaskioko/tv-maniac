package com.thomaskioko.tvmaniac.util

import io.github.aakira.napier.Napier
import io.ktor.client.features.ResponseException
import io.ktor.client.features.ServerResponseException
import io.ktor.client.statement.HttpResponse

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
            502 -> ExceptionHandler(errorMessage = "Internal error!")
            else -> ExceptionHandler.parseException(this)
        }
    }
    is ServerResponseException -> ExceptionHandler(errorMessage = getErrorMessage())
    is NullPointerException -> ExceptionHandler(errorMessage = getErrorMessage())
    else -> ExceptionHandler(errorMessage = getErrorMessage())
}

fun Throwable.getErrorMessage(): String {
    return if (BuildConfig().isDebug()) {
        Napier.e("Exception:: $message", this)
        message ?: "Something went wrong"
    } else {
        "Something went wrong"
    }
}

fun ResponseException.getErrorMessage(): String {
    return if (BuildConfig().isDebug()) {
        "Server Error: ${response.status.value} /n $message"
    } else {
        "Connection to server failed."
    }
}
