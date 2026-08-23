package com.thomaskioko.tvmaniac.deeplink.testing

import com.thomaskioko.tvmaniac.deeplink.api.DeepLink
import com.thomaskioko.tvmaniac.deeplink.api.DeepLinkUrls

public class FakeDeepLinkUrls : DeepLinkUrls {

    private val urls = mutableMapOf<DeepLink, String?>()

    public fun setUrl(deepLink: DeepLink, url: String?) {
        urls[deepLink] = url
    }

    override fun urlFor(deepLink: DeepLink): String? = urls[deepLink]
}
