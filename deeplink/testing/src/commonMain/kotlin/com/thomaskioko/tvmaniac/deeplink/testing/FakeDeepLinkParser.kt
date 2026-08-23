package com.thomaskioko.tvmaniac.deeplink.testing

import com.thomaskioko.tvmaniac.deeplink.api.DeepLink
import com.thomaskioko.tvmaniac.deeplink.api.DeepLinkParser

public class FakeDeepLinkParser : DeepLinkParser {

    private val results = mutableMapOf<String, DeepLink?>()

    public var lastParsedUrl: String? = null
        private set

    public fun setResult(url: String, deepLink: DeepLink?) {
        results[url] = deepLink
    }

    override fun parse(url: String?): DeepLink? {
        lastParsedUrl = url
        return results[url]
    }
}
