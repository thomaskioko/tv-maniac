package com.thomaskioko.tvmaniac.compose.theme

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.text.font.FontWeight
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
internal class TypeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `should bind each work sans face to its true weight`() {
        lateinit var family: FontFamily
        composeTestRule.setContent {
            family = workSansFontFamily()
        }

        val weights = (family as FontListFontFamily).fonts.map(Font::weight)

        weights shouldContainExactlyInAnyOrder listOf(
            FontWeight.W100,
            FontWeight.W300,
            FontWeight.W400,
            FontWeight.W500,
            FontWeight.W600,
            FontWeight.W700,
            FontWeight.W800,
        )
    }
}
