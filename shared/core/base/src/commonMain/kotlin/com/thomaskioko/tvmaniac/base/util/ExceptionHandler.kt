package com.thomaskioko.tvmaniac.base.util

interface ExceptionHandler {

    fun resolveError(throwable: Throwable): String
}