package com.thomaskioko.tvmaniac.featureflags.testing

import com.thomaskioko.tvmaniac.featureflags.RemoteConfigBridge

public class FakeRemoteConfigBridge : RemoteConfigBridge {

    private val values: MutableMap<String, Boolean> = mutableMapOf()
    private val defaults: MutableMap<String, Boolean> = mutableMapOf()
    private var listener: (() -> Unit)? = null
    private var fetchResult: Boolean = true
    public var lastMinimumFetchIntervalSeconds: Long? = null
        private set

    public fun setValue(key: String, value: Boolean) {
        values[key] = value
    }

    public fun setFetchResult(success: Boolean) {
        fetchResult = success
    }

    public fun triggerConfigUpdate() {
        listener?.invoke()
    }

    override fun setMinimumFetchIntervalSeconds(seconds: Long) {
        lastMinimumFetchIntervalSeconds = seconds
    }

    override fun fetchAndActivate(onResult: (Boolean) -> Unit) {
        onResult(fetchResult)
    }

    override fun getBoolean(key: String): Boolean = values[key] ?: defaults[key] ?: false

    override fun setDefaults(defaults: Map<String, Boolean>) {
        this.defaults.clear()
        this.defaults.putAll(defaults)
    }

    override fun addOnConfigUpdateListener(onUpdate: () -> Unit) {
        listener = onUpdate
    }
}
