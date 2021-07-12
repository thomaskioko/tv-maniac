package com.thomaskioko.tvmaniac.util

import io.github.aakira.napier.Napier
import io.ktor.client.statement.*

open class ExceptionHandler(
    val errorCode: Int = -1,
    val errorMessage: String,
) : Exception() {

    override val message: String
        get() = cause.message ?: "Something went wrong"

    override val cause: Throwable
        get() = Throwable(errorMessage)

    companion object {
        fun parseException(response: HttpResponse): ExceptionHandler {
            return ExceptionHandler(response.status.value, "unexpected error!!ً")
        }
    }
}


fun Throwable.resolveError() = when (this) {
    is HttpResponse -> {
        when (status.value) {
            502 -> ExceptionHandler(status.value, "Internal error!")
            401 -> ExceptionHandler(errorMessage = "Authentication error!")
            400 -> ExceptionHandler.parseException(this)
            else -> ExceptionHandler.parseException(this)
        }
    }
    else -> ExceptionHandler(errorMessage = "Something went wrong")
}

data class ErrorResponse(
    val errorDescription: String, // this is the translated error shown to the user directly from the API
    val causes: Map<String, String> = emptyMap() //this is for errors on specific field on a form
)
