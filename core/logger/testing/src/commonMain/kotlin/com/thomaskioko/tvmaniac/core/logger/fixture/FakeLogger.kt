package com.thomaskioko.tvmaniac.core.logger.fixture

import com.thomaskioko.tvmaniac.core.logger.Logger

public class FakeLogger : Logger {

    private val recordedErrorsList: MutableList<RecordedError> = mutableListOf()
    private val recordedWarningsList: MutableList<RecordedWarning> = mutableListOf()
    private val breadcrumbsList: MutableList<String> = mutableListOf()
    private val recordedExceptionsList: MutableList<RecordedException> = mutableListOf()
    private val customKeysMap: MutableMap<String, String> = mutableMapOf()
    private var recordedUserId: String? = null

    public val recordedErrors: List<RecordedError> get() = recordedErrorsList.toList()
    public val recordedWarnings: List<RecordedWarning> get() = recordedWarningsList.toList()
    public val breadcrumbs: List<String> get() = breadcrumbsList.toList()
    public val recordedExceptions: List<RecordedException> get() = recordedExceptionsList.toList()
    public val customKeys: Map<String, String> get() = customKeysMap.toMap()
    public val userId: String? get() = recordedUserId

    override fun setup(debugMode: Boolean) {}

    override fun debug(message: String) {}

    override fun debug(tag: String, message: String) {}

    override fun error(message: String, throwable: Throwable) {
        recordedErrorsList += RecordedError(tag = null, message = message, throwable = throwable, keys = emptyMap())
    }

    override fun error(tag: String, message: String) {
        breadcrumbsList += "[$tag] $message"
    }

    override fun error(tag: String, message: String, throwable: Throwable, keys: Map<String, String>) {
        recordedErrorsList += RecordedError(tag = tag, message = message, throwable = throwable, keys = keys)
    }

    override fun info(message: String, throwable: Throwable) {}

    override fun info(tag: String, message: String) {}

    override fun warning(message: String) {}

    override fun warning(tag: String, message: String) {}

    override fun warning(tag: String, message: String, throwable: Throwable, keys: Map<String, String>) {
        recordedWarningsList += RecordedWarning(tag = tag, message = message, throwable = throwable, keys = keys)
    }

    override fun verbose(message: String) {}

    override fun verbose(tag: String, message: String) {}

    override fun recordException(throwable: Throwable, keys: Map<String, String>) {
        recordedExceptionsList += RecordedException(throwable = throwable, keys = keys)
    }

    override fun setUserId(userId: String) {
        recordedUserId = userId
    }

    override fun setCustomKey(key: String, value: String) {
        customKeysMap[key] = value
    }

    public data class RecordedError(
        val tag: String?,
        val message: String,
        val throwable: Throwable,
        val keys: Map<String, String>,
    )

    public data class RecordedWarning(
        val tag: String,
        val message: String,
        val throwable: Throwable,
        val keys: Map<String, String>,
    )

    public data class RecordedException(
        val throwable: Throwable,
        val keys: Map<String, String>,
    )
}
