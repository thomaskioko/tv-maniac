package com.thomaskioko.root.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DeepLinkParserTest {

    @Test
    fun `should return show details given a show url`() {
        DeepLinkParser.parse("tvmaniac://show/1399") shouldBe
            DeepLinkDestination.ShowDetails(showId = 1399)
    }

    @Test
    fun `should return episode given an episode url`() {
        DeepLinkParser.parse("tvmaniac://episode/1399/1/2") shouldBe
            DeepLinkDestination.Episode(
                showId = 1399,
                seasonNumber = 1,
                episodeNumber = 2,
            )
    }

    @Test
    fun `should ignore a query string given a url carrying one`() {
        DeepLinkParser.parse("tvmaniac://show/1399?source=widget") shouldBe
            DeepLinkDestination.ShowDetails(showId = 1399)
    }

    @Test
    fun `should return null given an unknown host`() {
        DeepLinkParser.parse("tvmaniac://season/1399/1") shouldBe null
    }

    @Test
    fun `should return null given the oauth redirect`() {
        DeepLinkParser.parse("tvmaniac://auth/trakt") shouldBe null
    }

    @Test
    fun `should return null given a missing segment`() {
        DeepLinkParser.parse("tvmaniac://episode/1399/1") shouldBe null
    }

    @Test
    fun `should return null given a trailing segment`() {
        DeepLinkParser.parse("tvmaniac://show/1399/extra") shouldBe null
    }

    @Test
    fun `should return null given a non numeric id`() {
        DeepLinkParser.parse("tvmaniac://show/breaking-bad") shouldBe null
    }

    @Test
    fun `should return null given a non numeric episode number`() {
        DeepLinkParser.parse("tvmaniac://episode/1399/1/pilot") shouldBe null
    }

    @Test
    fun `should return null given another scheme`() {
        DeepLinkParser.parse("https://show/1399") shouldBe null
    }

    @Test
    fun `should return null given no id`() {
        DeepLinkParser.parse("tvmaniac://show") shouldBe null
    }

    @Test
    fun `should return null given a blank or absent url`() {
        DeepLinkParser.parse(null) shouldBe null
        DeepLinkParser.parse("") shouldBe null
        DeepLinkParser.parse("tvmaniac://") shouldBe null
    }
}
