package com.thomaskioko.tvmaniac.i18n.generator

import com.thomaskioko.tvmaniac.i18n.MR
import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey.EpisodeCount
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey.CdShowPosterImage
import com.thomaskioko.tvmaniac.i18n.testing.util.BaseLocalizerTest
import com.thomaskioko.tvmaniac.i18n.testing.util.getPlural
import com.thomaskioko.tvmaniac.i18n.testing.util.getString
import dev.icerock.moko.resources.desc.desc
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class ResourceTest : BaseLocalizerTest() {

    @Test
    fun `should return localized string for valid key`() = runTest {
        val result = MR.strings.button_error_retry.desc().getString()

        result shouldBe "Retry"
    }

    @Test
    fun `should return localized string for generated object`() = runTest {
        val result = StringResourceKey.ButtonErrorRetry.getString()

        result shouldBe "Retry"
    }

    @Test
    fun `should return formatted string when arguments are provided`() {
        val result = CdShowPosterImage.getString("Breaking Bad")

        result shouldBe "Poster image for Breaking Bad"
    }

    @Test
    fun `should return localized string for plurals`() {
        val quantities = listOf(1, 2, 5)
        val expectedResults = listOf("1 Episode", "2 Episodes", "5 Episodes")

        quantities.zip(expectedResults).forEach { (quantity, expected) ->
            val result = EpisodeCount.getPlural(quantity)

            result shouldBe expected
        }
    }
}
