package com.thomaskioko.tvmaniac.core.util

import co.touchlab.kermit.Logger
import io.ktor.client.plugins.ServerResponseException
import java.net.UnknownHostException

actual object ExceptionHandler : Exception() {

    override val message: String
        get() = cause?.message ?: "Something went wrong"

    actual fun Throwable.resolveError() = when (this) {
        is UnknownHostException -> "No Internet Connection!"
        is ServerResponseException, is NullPointerException -> getErrorMessage()
        else -> getErrorMessage()
    }

    private fun Throwable.getErrorMessage(): String {
        Logger.e("Exception:: $message", this)
        return message ?: "Something went wrong"
    }
}
