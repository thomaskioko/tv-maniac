package com.thomaskioko.tvmaniac.shared.core.util

import co.touchlab.kermit.Logger
import io.ktor.client.features.ResponseException
import io.ktor.client.features.ServerResponseException

open class ExceptionHandler(
    val errorMessage: String,
) : Exception() {

    override val message: String
        get() = cause.message ?: "Something went wrong"

    override val cause: Throwable
        get() = Throwable(errorMessage)
}

fun Throwable.resolveError() = when (this) {
    is ServerResponseException -> ExceptionHandler(errorMessage = getErrorMessage())
    is NullPointerException -> ExceptionHandler(errorMessage = getErrorMessage())
    else -> ExceptionHandler(errorMessage = getErrorMessage())
}

fun Throwable.getErrorMessage(): String {
    Logger.e("Exception:: $message", this)
    return message ?: "Something went wrong"
}

fun ResponseException.getErrorMessage(): String {
    Logger.e("ResponseException:: $message", this)
    return "Server Error: ${response.status.value} /n $message"
}
