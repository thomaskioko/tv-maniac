package com.thomaskioko.tvmaniac.core.logger.fixture

import com.thomaskioko.tvmaniac.core.logger.CrashReporter

public class FakeCrashReporter : CrashReporter {

    private val recordedExceptionsList: MutableList<RecordedException> = mutableListOf()
    private val breadcrumbsList: MutableList<String> = mutableListOf()
    private val customKeysMap: MutableMap<String, String> = mutableMapOf()
    private var recordedUserId: String? = null
    private var collectionEnabled: Boolean = true

    public val recordedExceptions: List<RecordedException> get() = recordedExceptionsList.toList()
    public val breadcrumbs: List<String> get() = breadcrumbsList.toList()
    public val customKeys: Map<String, String> get() = customKeysMap.toMap()
    public val userId: String? get() = recordedUserId
    public val isCollectionEnabled: Boolean get() = collectionEnabled

    override fun setCollectionEnabled(enabled: Boolean) {
        collectionEnabled = enabled
    }

    override fun recordException(throwable: Throwable, keys: Map<String, String>) {
        recordedExceptionsList += RecordedException(throwable, keys)
    }

    override fun setCustomKey(key: String, value: String) {
        customKeysMap[key] = value
    }

    override fun setUserId(userId: String) {
        recordedUserId = userId
    }

    override fun log(message: String) {
        breadcrumbsList += message
    }

    public data class RecordedException(
        val throwable: Throwable,
        val keys: Map<String, String>,
    )
}
