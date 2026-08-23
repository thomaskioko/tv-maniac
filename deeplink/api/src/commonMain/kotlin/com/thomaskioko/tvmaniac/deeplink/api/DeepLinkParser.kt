package com.thomaskioko.tvmaniac.deeplink.api

public interface DeepLinkParser {
    public fun parse(url: String?): DeepLink?
}
