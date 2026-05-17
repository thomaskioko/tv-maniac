package com.thomaskioko.tvmaniac.featureflags

public interface RemoteConfigBridge {
    public fun setMinimumFetchIntervalSeconds(seconds: Long)
    public fun fetchAndActivate(onResult: (Boolean) -> Unit)
    public fun getBoolean(key: String): Boolean
    public fun setDefaults(defaults: Map<String, Boolean>)
    public fun addOnConfigUpdateListener(onUpdate: () -> Unit)
}
