package com.thomaskioko.tvmaniac.core.util

expect object ExceptionHandler{

    fun Throwable.resolveError(): String
}