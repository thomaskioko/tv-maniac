package com.thomaskioko.tvmaniac.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ColumnScope
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.semantics.semantics
import androidx.glance.semantics.testTag
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.thomaskioko.tvmaniac.testtags.widget.WidgetTestTags

@Composable
internal fun UpNextPosterWidgetContent(
    emptyMessage: String,
    item: UpNextWidgetItem?,
    openApp: Action,
    itemAction: (UpNextWidgetItem) -> Action,
    modifier: GlanceModifier = GlanceModifier,
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .background(GlanceTheme.colors.surface)
            .then(modifier)
            .padding(TILE_PADDING)
            .clickable(if (item == null) openApp else itemAction(item)),
        horizontalAlignment = Alignment.Start,
    ) {
        if (item == null) {
            EmptyTile(message = emptyMessage)
            return@Column
        }

        Poster(item = item)

        Spacer(modifier = GlanceModifier.height(TILE_SPACING))

        Text(
            text = item.showName,
            maxLines = 1,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
        Text(
            text = item.seasonEpisodeLabel,
            maxLines = 1,
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 12.sp,
            ),
        )
    }
}

@Composable
private fun ColumnScope.Poster(item: UpNextWidgetItem) {
    val modifier = GlanceModifier
        .fillMaxWidth()
        .defaultWeight()
        .cornerRadius(TILE_POSTER_RADIUS)
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
private fun ColumnScope.EmptyTile(message: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = GlanceModifier
            .fillMaxSize()
            .defaultWeight()
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

private val TILE_PADDING = 8.dp
private val TILE_SPACING = 6.dp
private val TILE_POSTER_RADIUS = 8.dp
