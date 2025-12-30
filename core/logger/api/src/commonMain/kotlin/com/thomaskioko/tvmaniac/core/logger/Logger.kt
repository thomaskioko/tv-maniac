package com.thomaskioko.tvmaniac.core.logger

public interface Logger {

    public fun setup(debugMode: Boolean)

    public fun debug(message: String)

    public fun debug(tag: String, message: String)

    public fun error(message: String, throwable: Throwable)

    public fun error(tag: String, message: String)

    public fun info(message: String, throwable: Throwable)

    public fun info(tag: String, message: String)

    public fun warning(message: String)

    public fun warning(tag: String, message: String)

    public fun verbose(message: String)

    public fun verbose(tag: String, message: String)
}
