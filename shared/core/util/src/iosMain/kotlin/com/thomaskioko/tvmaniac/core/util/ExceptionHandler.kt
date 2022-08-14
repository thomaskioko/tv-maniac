package com.thomaskioko.tvmaniac.core.util

actual object ExceptionHandler {

    actual fun Throwable.resolveError(): String {
        //TODO:: Implement exception handling
        return "Something went wrong"
    }
}