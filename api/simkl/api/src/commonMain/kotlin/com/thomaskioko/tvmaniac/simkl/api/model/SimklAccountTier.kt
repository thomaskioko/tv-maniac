package com.thomaskioko.tvmaniac.simkl.api.model

public enum class SimklAccountTier {
    FREE,
    PREMIUM,
    ;

    public companion object {
        public fun fromRaw(type: String?): SimklAccountTier = when (type) {
            "pro", "vip" -> PREMIUM
            else -> FREE
        }
    }
}
