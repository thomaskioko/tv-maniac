package com.thomaskioko.tvmaniac.util

interface NetworkExceptionHandler {

  fun resolveError(throwable: Throwable): String?
}
