package com.thomaskioko.tvmaniac.core.networkutil

interface NetworkExceptionHandler {

  fun resolveError(throwable: Throwable): String?
}
