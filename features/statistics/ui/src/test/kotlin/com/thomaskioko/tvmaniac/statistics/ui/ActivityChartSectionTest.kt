package com.thomaskioko.tvmaniac.statistics.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.statistics.presenter.model.ActivityBar
import com.thomaskioko.tvmaniac.statistics.ui.components.ActivityChartSection
import com.thomaskioko.tvmaniac.testtags.statistics.StatisticsTestTags
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
class ActivityChartSectionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val bars = persistentListOf(
        ActivityBar(label = "Mon", episodeCount = 12, caption = "12 episodes", fraction = 0.6f),
        ActivityBar(label = "Sat", episodeCount = 20, caption = "20 episodes", fraction = 1f),
    )

    @Test
    fun `should show no tooltip until a bar is tapped`() {
        showChart()

        composeTestRule.onNodeWithTag(StatisticsTestTags.ACTIVITY_TOOLTIP_TEST_TAG).assertDoesNotExist()
    }

    @Test
    fun `should name the count and the bar given a bar is tapped`() {
        showChart()

        composeTestRule.onNodeWithTag(StatisticsTestTags.activityBar("weekday", "Sat")).performClick()

        composeTestRule.onNodeWithTag(StatisticsTestTags.ACTIVITY_TOOLTIP_TEST_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithText("20 episodes").assertIsDisplayed()
    }

    @Test
    fun `should move the tooltip given another bar is tapped`() {
        showChart()

        composeTestRule.onNodeWithTag(StatisticsTestTags.activityBar("weekday", "Sat")).performClick()
        composeTestRule.onNodeWithTag(StatisticsTestTags.activityBar("weekday", "Mon")).performClick()

        composeTestRule.onNodeWithText("12 episodes").assertIsDisplayed()
    }

    @Test
    fun `should hide the tooltip given the same bar is tapped again`() {
        showChart()

        composeTestRule.onNodeWithTag(StatisticsTestTags.activityBar("weekday", "Sat")).performClick()
        composeTestRule.onNodeWithTag(StatisticsTestTags.activityBar("weekday", "Sat")).performClick()

        composeTestRule.onNodeWithTag(StatisticsTestTags.ACTIVITY_TOOLTIP_TEST_TAG).assertDoesNotExist()
    }

    @Test
    fun `should write the axis labels in full given a long run of years`() {
        showYears(2008..2026)

        listOf("2008", "2014", "2020", "2026").forEach { year ->
            composeTestRule.onNodeWithText(year).assertIsDisplayed()
        }
    }

    @Test
    fun `should label the first and last year given the span does not divide evenly`() {
        showYears(2016..2026)

        composeTestRule.onNodeWithText("2016").assertIsDisplayed()
        composeTestRule.onNodeWithText("2026").assertIsDisplayed()
    }

    private fun showYears(years: IntRange) {
        val bars = years.map { year ->
            ActivityBar(
                label = year.toString(),
                episodeCount = 1,
                caption = "1 episode",
                fraction = 0.5f,
            )
        }.toImmutableList()

        composeTestRule.setContent {
            TvManiacBackground {
                ActivityChartSection(
                    bars = bars,
                    title = "Episodes by year",
                    sectionTestTag = StatisticsTestTags.YEARLY_ACTIVITY_TEST_TAG,
                    sectionName = "yearly",
                    showAxisLabels = true,
                )
            }
        }
    }

    private fun showChart() {
        composeTestRule.setContent {
            TvManiacBackground {
                ActivityChartSection(
                    bars = bars,
                    title = "Episodes by day of the week",
                    sectionTestTag = StatisticsTestTags.WEEKDAY_ACTIVITY_TEST_TAG,
                    sectionName = "weekday",
                )
            }
        }
    }
}
