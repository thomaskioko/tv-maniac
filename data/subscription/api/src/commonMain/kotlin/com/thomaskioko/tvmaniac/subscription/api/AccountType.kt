package com.thomaskioko.tvmaniac.subscription.api

public enum class AccountType {
    Premium,
    Free,
    None,
    ;

    public companion object {
        public fun fromName(value: String?): AccountType =
            entries.find { it.name == value } ?: None
    }
}
