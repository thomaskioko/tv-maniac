package com.thomaskioko.tvmaniac.core.deeplink.implementation

import com.thomaskioko.tvmaniac.core.deeplink.api.DeepLink
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DefaultDeepLinkParserTest {

    private val parser = DefaultDeepLinkParser()

    @Test
    fun `should return show details given a show url`() {
        parser.parse("tvmaniac://show/1399") shouldBe
            DeepLink.ShowDetails(showId = 1399)
    }

    @Test
    fun `should return episode given an episode url`() {
        parser.parse("tvmaniac://episode/1399/1/2") shouldBe
            DeepLink.Episode(
                showId = 1399,
                seasonNumber = 1,
                episodeNumber = 2,
            )
    }

    @Test
    fun `should ignore a query string given a url carrying one`() {
        parser.parse("tvmaniac://show/1399?source=widget") shouldBe
            DeepLink.ShowDetails(showId = 1399)
    }

    @Test
    fun `should return null given an unknown host`() {
        parser.parse("tvmaniac://season/1399/1") shouldBe null
    }

    @Test
    fun `should return null given the oauth redirect`() {
        parser.parse("tvmaniac://auth/trakt") shouldBe null
    }

    @Test
    fun `should return null given a missing segment`() {
        parser.parse("tvmaniac://episode/1399/1") shouldBe null
    }

    @Test
    fun `should return null given a trailing segment`() {
        parser.parse("tvmaniac://show/1399/extra") shouldBe null
    }

    @Test
    fun `should return null given a non numeric id`() {
        parser.parse("tvmaniac://show/breaking-bad") shouldBe null
    }

    @Test
    fun `should return null given a non numeric episode number`() {
        parser.parse("tvmaniac://episode/1399/1/pilot") shouldBe null
    }

    @Test
    fun `should return null given another scheme`() {
        parser.parse("https://show/1399") shouldBe null
    }

    @Test
    fun `should return null given no id`() {
        parser.parse("tvmaniac://show") shouldBe null
    }

    @Test
    fun `should return null given a blank or absent url`() {
        parser.parse(null) shouldBe null
        parser.parse("") shouldBe null
        parser.parse("tvmaniac://") shouldBe null
    }
}
