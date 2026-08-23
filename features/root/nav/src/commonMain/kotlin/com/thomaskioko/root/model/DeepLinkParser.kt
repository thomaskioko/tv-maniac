package com.thomaskioko.root.model

public interface DeepLinkParser {
    public fun parse(url: String?): DeepLinkDestination?
}
