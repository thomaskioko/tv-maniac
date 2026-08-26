package com.thomaskioko.tvmaniac.core.deeplink.api

public interface DeepLinkParser {
    public fun parse(url: String?): DeepLink?
}
