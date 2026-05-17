package com.thomaskioko.tvmaniac.featureflags.model

public enum class FeatureFlag(
    public val key: String,
    public val defaultValue: Boolean,
) {
    SIMKL_LOGIN_ENABLED(key = "simkl_login_enabled", defaultValue = false),
}
