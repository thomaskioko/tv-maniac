package com.thomaskioko.tvmaniac.core.logger.fixture

import com.thomaskioko.tvmaniac.core.logger.Logger

public class FakeLogger : Logger {
    override fun setup(debugMode: Boolean) {}

    override fun debug(message: String) {}

    override fun debug(tag: String, message: String) {}

    override fun error(message: String, throwable: Throwable) {}

    override fun error(tag: String, message: String) {}

    override fun info(message: String, throwable: Throwable) {}

    override fun info(tag: String, message: String) {}

    override fun warning(message: String) {}

    override fun warning(tag: String, message: String) {}

    override fun verbose(message: String) {}

    override fun verbose(tag: String, message: String) {}
}
