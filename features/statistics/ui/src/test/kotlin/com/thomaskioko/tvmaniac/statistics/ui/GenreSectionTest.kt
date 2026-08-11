package com.thomaskioko.tvmaniac.statistics.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.statistics.presenter.model.GenreSlice
import com.thomaskioko.tvmaniac.statistics.ui.components.GenreSection
import com.thomaskioko.tvmaniac.testtags.statistics.StatisticsTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
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
class GenreSectionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val genres = persistentListOf(
        GenreSlice(name = "Drama", showCount = 24, caption = "24 shows", fraction = 1f),
        GenreSlice(name = "Comedy", showCount = 11, caption = "11 shows", fraction = 0.45f),
        GenreSlice(name = "Other", showCount = 12, caption = "12 shows", fraction = 0.5f),
    )

    @Test
    fun `should show a row for every genre`() {
        showGenres(genres)

        composeTestRule.onNodeWithTag(StatisticsTestTags.genreRow("Drama")).assertIsDisplayed()
        composeTestRule.onNodeWithTag(StatisticsTestTags.genreRow("Comedy")).assertIsDisplayed()
        composeTestRule.onNodeWithTag(StatisticsTestTags.genreRow("Other")).assertIsDisplayed()
    }

    @Test
    fun `should name each genre and its show count`() {
        showGenres(genres)

        composeTestRule.onNodeWithText("Drama").assertIsDisplayed()
        composeTestRule.onNodeWithText("24 shows").assertIsDisplayed()
    }

    @Test
    fun `should show the empty message given no genres`() {
        showGenres(persistentListOf())

        composeTestRule.onNodeWithTag(StatisticsTestTags.GENRES_EMPTY_MESSAGE_TEST_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithText(EMPTY_MESSAGE).assertIsDisplayed()
    }

    @Test
    fun `should leave out the empty message given genres exist`() {
        showGenres(genres)

        composeTestRule.onNodeWithTag(StatisticsTestTags.GENRES_EMPTY_MESSAGE_TEST_TAG).assertDoesNotExist()
    }

    private fun showGenres(genres: ImmutableList<GenreSlice>) {
        composeTestRule.setContent {
            TvManiacBackground {
                GenreSection(
                    genres = genres,
                    title = "Genres you watch",
                    emptyMessage = EMPTY_MESSAGE,
                )
            }
        }
    }

    private companion object {
        const val EMPTY_MESSAGE = "We do not know the genres of the shows you have watched yet."
    }
}
