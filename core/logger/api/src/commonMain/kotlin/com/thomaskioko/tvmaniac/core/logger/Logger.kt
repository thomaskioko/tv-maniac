package com.thomaskioko.tvmaniac.core.logger

interface Logger {

  fun setup(debugMode: Boolean)

  fun debug(message: String)

  fun debug(tag: String, message: String)

  fun error(message: String, throwable: Throwable)

  fun error(tag: String, message: String)

  fun info(message: String, throwable: Throwable)

  fun info(tag: String, message: String)

  fun warning(message: String)

  fun warning(tag: String, message: String)

  fun verbose(message: String)

  fun verbose(tag: String, message: String)
}
