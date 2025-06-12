package com.thomaskioko.tvmaniac.i18n

import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey.EpisodeCount
import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey.WatchedEpisodesCount
import com.thomaskioko.tvmaniac.i18n.StringResourceKey.CdShowPosterImage
import com.thomaskioko.tvmaniac.i18n.testing.util.IgnoreIos
import com.thomaskioko.tvmaniac.i18n.util.BaseResourceTests
import io.kotest.matchers.shouldBe
import kotlin.test.Test

@IgnoreIos
class MokoLocalizerTest : BaseResourceTests() {

    @Test
    fun `should return localized string for valid key`() {
        val result = localizer.getString(StringResourceKey.ButtonErrorRetry)

        result shouldBe "Retry"
    }

    @Test
    fun `should return formatted string when arguments are provided`() {
        val result = localizer.getString(CdShowPosterImage, "Breaking Bad")

        result shouldBe "Poster image for Breaking Bad"
    }

    @Test
    fun `should return localized string for plurals`() {
        val quantities = listOf(1, 2, 5)
        val expectedResults = listOf("1 Episode", "2 Episodes", "5 Episodes")

        quantities.zip(expectedResults).forEach { (quantity, expected) ->
            val result = localizer.getPlural(EpisodeCount, quantity)

            result shouldBe expected
        }
    }

    @Test
    fun `should return localized plural string with multiple arguments`() {
        val showName = "Breaking Bad"
        val quantities = listOf(1, 2, 5)
        val expectedResults = listOf(
            "1 episode of Breaking Bad left.",
            "2 episodes of Breaking Bad left.",
            "5 episodes of Breaking Bad left.",
        )

        quantities.zip(expectedResults).forEach { (quantity, expected) ->
            val result = localizer.getPlural(WatchedEpisodesCount, quantity, quantity, showName)

            result shouldBe expected
        }
    }
}
