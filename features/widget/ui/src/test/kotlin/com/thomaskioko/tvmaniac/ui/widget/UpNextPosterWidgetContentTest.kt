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
import androidx.glance.testing.unit.hasAnyDescendant
import androidx.glance.testing.unit.hasContentDescriptionEqualTo
import androidx.glance.testing.unit.hasTestTag
import androidx.glance.testing.unit.hasText
import androidx.test.core.app.ApplicationProvider
import com.thomaskioko.tvmaniac.testtags.widget.WidgetTestTags
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UpNextPosterWidgetContentTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `should show the show name and season label given an episode`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(TILE)
        provideComposable { Content(item) }

        onNode(hasText("Breaking Bad")).assertExists()
        onNode(hasText("S01 | E01")).assertExists()
    }

    @Test
    fun `should name the show on the poster`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(TILE)
        provideComposable { Content(item) }

        onNode(hasContentDescriptionEqualTo("Breaking Bad")).assertExists()
        onNode(hasTestTag(WidgetTestTags.poster(BREAKING_BAD))).assertExists()
    }

    @Test
    fun `should open the episode given the tile is tapped`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(TILE)
        provideComposable { Content(item) }

        onNode(hasStartActivityClickAction(intentFor(item))).assertExists()
    }

    @Test
    fun `should show the empty message given no episode`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(TILE)
        provideComposable { Content(null) }

        onNode(hasTestTag(WidgetTestTags.EMPTY_STATE_TEST_TAG))
            .assert(hasAnyDescendant(hasText(EMPTY_MESSAGE)))
    }

    @Test
    fun `should show no poster given no episode`() = runGlanceAppWidgetUnitTest {
        setContext(context)
        setAppWidgetSize(TILE)
        provideComposable { Content(null) }

        onNode(hasTestTag(WidgetTestTags.poster(BREAKING_BAD))).assertDoesNotExist()
    }

    @Composable
    private fun Content(item: UpNextWidgetItem?) {
        UpNextPosterWidgetContent(
            emptyMessage = EMPTY_MESSAGE,
            item = item,
            openApp = actionStartActivity(Intent(Intent.ACTION_MAIN)),
            itemAction = { actionStartActivity(intentFor(it)) },
        )
    }

    private fun intentFor(item: UpNextWidgetItem): Intent =
        Intent(Intent.ACTION_VIEW, item.url?.toUri())

    private val item = UpNextWidgetItem(
        showId = BREAKING_BAD,
        showName = "Breaking Bad",
        episodeName = "Pilot",
        seasonEpisodeLabel = "S01 | E01",
        poster = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
        url = "tvmaniac://episode/$BREAKING_BAD/1/1",
    )

    private companion object {
        private const val BREAKING_BAD = 1396L
        private const val EMPTY_MESSAGE = "Nothing to watch next. Track a show to see it here."

        private val TILE = DpSize(180.dp, 230.dp)
    }
}
