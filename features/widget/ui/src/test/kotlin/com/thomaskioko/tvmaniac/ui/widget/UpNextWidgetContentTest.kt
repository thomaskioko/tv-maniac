package com.thomaskioko.tvmaniac.ui.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.testing.unit.hasStartActivityClickAction
import androidx.glance.appwidget.testing.unit.runGlanceAppWidgetUnitTest
import androidx.glance.testing.unit.assertHasText
import androidx.glance.testing.unit.hasAnyDescendant
import androidx.glance.testing.unit.hasContentDescriptionEqualTo
import androidx.glance.testing.unit.hasTestTag
import androidx.glance.testing.unit.hasText
import androidx.test.core.app.ApplicationProvider
import com.thomaskioko.tvmaniac.testtags.widget.WidgetTestTags
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UpNextWidgetContentTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `should show one episode given a short size`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(SHORT)
        provideComposable { Content(items) }

        onNode(hasTestTag(WidgetTestTags.episodeRow(BREAKING_BAD))).assertExists()
        onNode(hasTestTag(WidgetTestTags.episodeRow(BETTER_CALL_SAUL))).assertDoesNotExist()
    }

    @Test
    fun `should show one episode given a wide but short size`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(WIDE_SHORT)
        provideComposable { Content(items) }

        onNode(hasTestTag(WidgetTestTags.episodeRow(BREAKING_BAD))).assertExists()
        onNode(hasTestTag(WidgetTestTags.episodeRow(BETTER_CALL_SAUL))).assertDoesNotExist()
    }

    @Test
    fun `should show three episodes given a tall size`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(TALL)
        provideComposable { Content(items) }

        items.take(3).forEach { onNode(hasTestTag(WidgetTestTags.episodeRow(it.showId))).assertExists() }
        onNode(hasTestTag(WidgetTestTags.episodeRow(THE_MANDALORIAN))).assertDoesNotExist()
    }

    @Test
    fun `should show four episodes given the tallest size`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(WIDE_TALLEST)
        provideComposable { Content(items) }

        items.forEach { onNode(hasTestTag(WidgetTestTags.episodeRow(it.showId))).assertExists() }
    }

    @Test
    fun `should show the episode name given a wide size`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(WIDE_TALL)
        provideComposable { Content(items) }

        onNode(hasText("Pilot")).assertExists()
    }

    @Test
    fun `should show the episode name given a narrow size`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(TALL)
        provideComposable { Content(items) }

        onNode(hasText("Pilot")).assertExists()
    }

    @Test
    fun `should hide the episode name given a wide strip`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(WIDE_STRIP)
        provideComposable { Content(items) }

        onNode(hasText("Pilot")).assertDoesNotExist()
    }

    @Test
    fun `should open its own episode given a row is tapped`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(WIDE_TALL)
        provideComposable { Content(items) }

        items.take(3).forEach { item ->
            onNode(hasTestTag(WidgetTestTags.episodeRow(item.showId)))
                .assert(hasStartActivityClickAction(intentFor(item)))
        }
    }

    @Test
    fun `should name the show on every poster`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(WIDE_TALL)
        provideComposable { Content(items) }

        onNode(hasContentDescriptionEqualTo("Breaking Bad")).assertExists()
    }

    @Test
    fun `should show the empty message given no episodes`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(WIDE_SHORT)
        provideComposable { Content(emptyList()) }

        onNode(hasTestTag(WidgetTestTags.EMPTY_STATE_TEST_TAG))
            .assert(hasAnyDescendant(hasText(EMPTY_MESSAGE)))
    }

    @Test
    fun `should show no episode row given no episodes`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(SHORT)
        provideComposable { Content(emptyList()) }

        onNode(hasTestTag(WidgetTestTags.episodeRow(BREAKING_BAD))).assertDoesNotExist()
    }

    @Test
    fun `should show the widget name given the shortest size`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(SHORT)
        provideComposable { Content(items) }

        onNode(hasTestTag(WidgetTestTags.TITLE_TEST_TAG)).assertHasText(TITLE)
    }

    @Test
    fun `should show one episode given a wide strip`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(WIDE_STRIP)
        provideComposable { Content(items) }

        onNode(hasTestTag(WidgetTestTags.episodeRow(BREAKING_BAD))).assertExists()
        onNode(hasTestTag(WidgetTestTags.episodeRow(BETTER_CALL_SAUL))).assertDoesNotExist()
    }

    @Test
    fun `should hide the widget name given a wide strip`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(WIDE_STRIP)
        provideComposable { Content(items) }

        onNode(hasTestTag(WidgetTestTags.TITLE_TEST_TAG)).assertDoesNotExist()
    }

    @Test
    fun `should show the season label given a wide strip`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(WIDE_STRIP)
        provideComposable { Content(items) }

        onNode(hasText("S01 | E01")).assertExists()
    }

    @Test
    fun `should fit the title and every row in one glance container`() {
        (1 + MAX_VISIBLE) shouldBeLessThanOrEqual MAX_CONTAINER_CHILDREN
    }

    @Test
    fun `should cap the episode count given a very tall size`() {
        visibleCount(DpSize(245.dp, 900.dp)) shouldBe MAX_VISIBLE
    }

    @Test
    fun `should show one episode given a size with room for none`() {
        visibleCount(DpSize(109.dp, 60.dp)) shouldBe 1
    }

    @Composable
    private fun Content(items: List<UpNextWidgetItem>) {
        UpNextWidgetContent(
            title = TITLE,
            emptyMessage = EMPTY_MESSAGE,
            items = items,
            openApp = actionStartActivity(Intent(Intent.ACTION_MAIN)),
            itemAction = { actionStartActivity(intentFor(it)) },
        )
    }

    private fun intentFor(item: UpNextWidgetItem): Intent =
        Intent(Intent.ACTION_VIEW, item.url?.toUri())

    private val items = listOf(
        item(BREAKING_BAD, "Breaking Bad", "Pilot", Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)),
        item(BETTER_CALL_SAUL, "Better Call Saul", "Uno"),
        item(GAME_OF_THRONES, "Game of Thrones", "Winter Is Coming"),
        item(THE_MANDALORIAN, "The Mandalorian", "Chapter 1"),
    )

    private fun item(
        showId: Long,
        showName: String,
        episodeName: String,
        poster: Bitmap? = null,
    ) = UpNextWidgetItem(
        showId = showId,
        showName = showName,
        episodeName = episodeName,
        seasonEpisodeLabel = "S01 | E01",
        poster = poster,
        url = "tvmaniac://episode/$showId/1/1",
    )

    private companion object {
        private const val MAX_CONTAINER_CHILDREN = 10

        private const val TITLE = "Up Next"
        private const val EMPTY_MESSAGE = "Nothing to watch next. Track a show to see it here."

        private const val BREAKING_BAD = 1396L
        private const val BETTER_CALL_SAUL = 60059L
        private const val GAME_OF_THRONES = 1399L
        private const val THE_MANDALORIAN = 82856L

        private val SHORT = DpSize(109.dp, 115.dp)
        private val WIDE_SHORT = DpSize(245.dp, 115.dp)
        private val TALL = DpSize(180.dp, 230.dp)
        private val WIDE_TALL = DpSize(245.dp, 230.dp)
        private val WIDE_TALLEST = DpSize(245.dp, 340.dp)
        private val WIDE_STRIP = DpSize(245.dp, 56.dp)
    }
}
