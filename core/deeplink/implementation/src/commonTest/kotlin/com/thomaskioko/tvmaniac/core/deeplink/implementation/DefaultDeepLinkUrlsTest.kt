package com.thomaskioko.tvmaniac.core.deeplink.implementation

import com.thomaskioko.tvmaniac.core.deeplink.api.DeepLink
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DefaultDeepLinkUrlsTest {

    private val urls = DefaultDeepLinkUrls()
    private val parser = DefaultDeepLinkParser()

    @Test
    fun `should build a show url`() {
        urls.urlFor(DeepLink.ShowDetails(showId = 1399)) shouldBe "tvmaniac://show/1399"
    }

    @Test
    fun `should build an episode url`() {
        urls.urlFor(
            DeepLink.Episode(showId = 1399, seasonNumber = 1, episodeNumber = 2),
        ) shouldBe "tvmaniac://episode/1399/1/2"
    }

    @Test
    fun `should return null given a destination with no url`() {
        urls.urlFor(DeepLink.DebugMenu) shouldBe null
        urls.urlFor(
            DeepLink.SeasonDetails(showId = 1399, seasonId = 2, seasonNumber = 1),
        ) shouldBe null
    }

    @Test
    fun `should parse back every url it builds`() {
        val destinations = listOf(
            DeepLink.ShowDetails(showId = 1399),
            DeepLink.Episode(showId = 1399, seasonNumber = 1, episodeNumber = 2),
        )

        destinations.forEach { destination ->
            parser.parse(urls.urlFor(destination)) shouldBe destination
        }
    }
}
