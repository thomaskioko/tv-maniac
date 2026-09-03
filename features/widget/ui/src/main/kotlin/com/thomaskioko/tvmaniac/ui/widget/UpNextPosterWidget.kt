package com.thomaskioko.tvmaniac.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.PreviewSizeMode
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.thomaskioko.tvmaniac.core.deeplink.api.DeepLink
import com.thomaskioko.tvmaniac.core.deeplink.api.DeepLinkUrls
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetShow
import com.thomaskioko.tvmaniac.i18n.MR.strings.widget_empty_watchlist
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.ui.widget.di.widgetGraph
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart

public class UpNextPosterWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override val previewSizeMode: PreviewSizeMode = SizeMode.Responsive(PREVIEW_SIZES)

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val graph = context.widgetGraph
        val interactor = graph.observeWidgetShowsInteractor
        val show = interactor.flow
            .onStart { interactor(Unit) }
            .first()
            .firstOrNull()

        val item = show?.toTileItem(context, graph.deepLinkUrls)
        val theme = context.widgetTheme()

        provideContent { PosterBody(context, item, theme) }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        val theme = context.widgetTheme()
        provideContent {
            PosterBody(
                context = context,
                item = previewItems(context).first(),
                theme = theme,
                modifier = GlanceModifier.systemCornerRadius(),
            )
        }
    }

    private companion object {
        private val PREVIEW_SIZES = setOf(DpSize(180.dp, 230.dp))
    }
}

@Composable
private fun PosterBody(
    context: Context,
    item: UpNextWidgetItem?,
    theme: AppTheme,
    modifier: GlanceModifier = GlanceModifier,
) {
    WidgetTheme(theme) {
        UpNextPosterWidgetContent(
            emptyMessage = widget_empty_watchlist.resolve(context),
            item = item,
            openApp = context.openAppAction(),
            itemAction = { context.openItemAction(it) },
            modifier = modifier,
        )
    }
}

private suspend fun WidgetShow.toTileItem(context: Context, urls: DeepLinkUrls): UpNextWidgetItem =
    UpNextWidgetItem(
        showId = tmdbId,
        showName = showName,
        episodeName = episodeName,
        seasonEpisodeLabel = context.seasonEpisodeLabel(seasonNumber, episodeNumber),
        poster = context.loadPoster(posterUrl, TILE_POSTER_WIDTH_PX, TILE_POSTER_HEIGHT_PX),
        url = urls.urlFor(
            DeepLink.Episode(
                showId = tmdbId,
                seasonNumber = seasonNumber,
                episodeNumber = episodeNumber,
            ),
        ),
    )
