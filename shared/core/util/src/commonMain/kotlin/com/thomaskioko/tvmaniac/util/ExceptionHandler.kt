package com.thomaskioko.tvmaniac.util

interface ExceptionHandler {

    fun resolveError(throwable: Throwable): String
}
