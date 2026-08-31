package com.thomaskioko.tvmaniac.ui.widget

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.semantics.semantics
import androidx.glance.semantics.testTag
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.thomaskioko.tvmaniac.testtags.widget.WidgetTestTags

@Composable
internal fun UpNextWidgetContent(
    title: String,
    emptyMessage: String,
    items: List<UpNextWidgetItem>,
    openApp: Action,
    itemAction: (UpNextWidgetItem) -> Action,
) {
    val size = LocalSize.current

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .background(GlanceTheme.colors.widgetBackground)
            .systemCornerRadius()
            .padding(WIDGET_PADDING),
    ) {
        Text(
            text = title,
            maxLines = 1,
            style = TextStyle(
                color = GlanceTheme.colors.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            ),
            modifier = GlanceModifier
                .padding(bottom = 4.dp)
                .semantics { testTag = WidgetTestTags.TITLE_TEST_TAG },
        )

        if (items.isEmpty()) {
            EmptyState(message = emptyMessage, openApp = openApp)
            return@Column
        }

        items.take(visibleCount(size)).forEachIndexed { index, item ->
            if (index > 0) Spacer(modifier = GlanceModifier.height(ROW_SPACING))
            EpisodeRow(
                item = item,
                showEpisodeName = size.height >= LARGE_HEIGHT,
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
                    .clickable(itemAction(item))
                    .semantics { testTag = WidgetTestTags.episodeRow(item.showId) },
            )
        }
    }
}

@Composable
private fun EpisodeRow(
    item: UpNextWidgetItem,
    showEpisodeName: Boolean,
    modifier: GlanceModifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Poster(item = item)

        Spacer(modifier = GlanceModifier.width(8.dp))

        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = item.showName,
                maxLines = 1,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
            Text(
                text = item.seasonEpisodeLabel,
                maxLines = 1,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 11.sp,
                ),
            )
            if (showEpisodeName && item.episodeName.isNotBlank()) {
                Text(
                    text = item.episodeName,
                    maxLines = 1,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 11.sp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun Poster(item: UpNextWidgetItem) {
    val modifier = GlanceModifier
        .width(POSTER_WIDTH)
        .fillMaxHeight()
        .cornerRadius(4.dp)
        .semantics { testTag = WidgetTestTags.poster(item.showId) }

    if (item.poster == null) {
        Box(modifier = modifier.background(GlanceTheme.colors.surfaceVariant)) {}
    } else {
        Image(
            provider = ImageProvider(item.poster),
            contentDescription = item.showName,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    }
}

@Composable
private fun EmptyState(message: String, openApp: Action) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = GlanceModifier
            .fillMaxSize()
            .clickable(openApp)
            .semantics { testTag = WidgetTestTags.EMPTY_STATE_TEST_TAG },
    ) {
        Text(
            text = message,
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

private fun GlanceModifier.systemCornerRadius(): GlanceModifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        cornerRadius(android.R.dimen.system_app_widget_background_radius)
    } else {
        this
    }

private fun visibleCount(size: DpSize): Int = when {
    size.height >= LARGE_HEIGHT -> 4
    size.width >= MEDIUM_WIDTH -> 2
    else -> 1
}

private val WIDGET_PADDING = 8.dp
private val ROW_SPACING = 4.dp
private val POSTER_WIDTH = 40.dp
private val MEDIUM_WIDTH = 200.dp
private val LARGE_HEIGHT = 200.dp
