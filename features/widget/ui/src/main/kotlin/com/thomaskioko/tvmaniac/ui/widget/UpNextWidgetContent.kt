package com.thomaskioko.tvmaniac.ui.widget

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
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
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
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
    modifier: GlanceModifier = GlanceModifier,
) {
    val size = LocalSize.current
    val compact = size.height < COMPACT_HEIGHT

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .background(GlanceTheme.colors.surface)
            .then(modifier)
            .padding(if (compact) COMPACT_PADDING else WIDGET_PADDING),
    ) {
        if (!compact) {
            Text(
                text = title,
                maxLines = 1,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = GlanceModifier
                    .padding(bottom = 4.dp)
                    .semantics { testTag = WidgetTestTags.TITLE_TEST_TAG },
            )
        }

        if (items.isEmpty()) {
            EmptyState(message = emptyMessage, openApp = openApp)
            return@Column
        }

        items.take(visibleCount(size)).forEachIndexed { index, item ->
            EpisodeRow(
                item = item,
                compact = compact,
                showEpisodeName = !compact,
                showInlineLabel = compact && size.width >= WIDE_WIDTH,
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
                    .padding(top = if (index == 0) 0.dp else ROW_SPACING)
                    .clickable(itemAction(item))
                    .semantics { testTag = WidgetTestTags.episodeRow(item.showId) },
            )
        }
    }
}

@Composable
private fun EpisodeRow(
    item: UpNextWidgetItem,
    compact: Boolean,
    showEpisodeName: Boolean,
    showInlineLabel: Boolean,
    modifier: GlanceModifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Poster(
            item = item,
            width = if (compact) COMPACT_POSTER_WIDTH else POSTER_WIDTH,
            height = if (compact) COMPACT_POSTER_HEIGHT else POSTER_HEIGHT,
        )

        Spacer(modifier = GlanceModifier.width(8.dp))

        if (showInlineLabel) {
            Text(
                text = item.showName,
                maxLines = 1,
                style = showNameStyle(),
                modifier = GlanceModifier.defaultWeight(),
            )
            Text(
                text = item.seasonEpisodeLabel,
                maxLines = 1,
                style = supportingStyle(),
            )
        } else {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(text = item.showName, maxLines = 1, style = showNameStyle())
                Text(text = item.seasonEpisodeLabel, maxLines = 1, style = supportingStyle())
                if (showEpisodeName && item.episodeName.isNotBlank()) {
                    Text(text = item.episodeName, maxLines = 1, style = supportingStyle())
                }
            }
        }
    }
}

@Composable
private fun showNameStyle(): TextStyle = TextStyle(
    color = GlanceTheme.colors.onSurface,
    fontSize = 13.sp,
    fontWeight = FontWeight.Medium,
)

@Composable
private fun supportingStyle(): TextStyle = TextStyle(
    color = GlanceTheme.colors.onSurfaceVariant,
    fontSize = 11.sp,
)

@Composable
private fun Poster(item: UpNextWidgetItem, width: Dp, height: Dp) {
    val modifier = GlanceModifier
        .size(width = width, height = height)
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

internal fun GlanceModifier.systemCornerRadius(): GlanceModifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        cornerRadius(android.R.dimen.system_app_widget_background_radius)
    } else {
        this
    }

internal fun visibleCount(size: DpSize): Int {
    if (size.height < COMPACT_HEIGHT) return 1
    val available = size.height - (WIDGET_PADDING * 2) - TITLE_HEIGHT
    return ((available + ROW_SPACING) / (POSTER_HEIGHT + ROW_SPACING)).toInt().coerceIn(1, MAX_VISIBLE)
}

internal const val MAX_VISIBLE: Int = 6

private val WIDGET_PADDING = 8.dp
private val COMPACT_PADDING = 4.dp
private val ROW_SPACING = 4.dp
private val TITLE_HEIGHT = 24.dp
private val POSTER_WIDTH = 40.dp
private val POSTER_HEIGHT = 60.dp
private val COMPACT_POSTER_WIDTH = 32.dp
private val COMPACT_POSTER_HEIGHT = 48.dp
private val COMPACT_HEIGHT = 100.dp
private val WIDE_WIDTH = 245.dp
