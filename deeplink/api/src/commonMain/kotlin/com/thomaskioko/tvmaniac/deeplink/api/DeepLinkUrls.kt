package com.thomaskioko.tvmaniac.deeplink.api

public interface DeepLinkUrls {
    public fun urlFor(deepLink: DeepLink): String?
}
