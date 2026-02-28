package com.thomaskioko.tvmaniac.core.logger

public interface Logger {

    public fun setup(debugMode: Boolean): Unit = Unit

    public fun debug(message: String): Unit = Unit

    public fun debug(tag: String, message: String): Unit = Unit

    public fun error(message: String, throwable: Throwable)

    public fun error(tag: String, message: String)

    public fun info(message: String, throwable: Throwable): Unit = Unit

    public fun info(tag: String, message: String): Unit = Unit

    public fun warning(message: String): Unit = Unit

    public fun warning(tag: String, message: String): Unit = Unit

    public fun verbose(message: String): Unit = Unit

    public fun verbose(tag: String, message: String): Unit = Unit

    public fun recordException(throwable: Throwable, tag: String = "") {}

    public fun setUserId(userId: String) {}

    public fun setCustomKey(key: String, value: String) {}
}
