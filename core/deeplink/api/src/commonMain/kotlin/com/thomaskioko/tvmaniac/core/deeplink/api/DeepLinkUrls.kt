package com.thomaskioko.tvmaniac.core.deeplink.api

public interface DeepLinkUrls {
    public fun urlFor(deepLink: DeepLink): String?
}
