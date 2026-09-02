package com.thomaskioko.tvmaniac.ui.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.PreviewSizeMode
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import com.thomaskioko.tvmaniac.core.deeplink.api.DeepLink
import com.thomaskioko.tvmaniac.core.deeplink.api.DeepLinkUrls
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetShow
import com.thomaskioko.tvmaniac.i18n.MR.strings.widget_empty_watchlist
import com.thomaskioko.tvmaniac.i18n.MR.strings.widget_season_episode
import com.thomaskioko.tvmaniac.i18n.MR.strings.widget_up_next_name
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.ui.widget.di.widgetGraph
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart

public class UpNextWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override val previewSizeMode: PreviewSizeMode = SizeMode.Responsive(PREVIEW_SIZES)

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val graph = context.widgetGraph
        val interactor = graph.observeWidgetShowsInteractor
        val shows = interactor.flow
            .onStart { interactor(Unit) }
            .first()
            .take(MAX_VISIBLE)

        val items = shows.map { it.toWidgetItem(context, graph.deepLinkUrls) }

        provideContent { WidgetBody(context, items) }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        provideContent { WidgetBody(context, previewItems(context), GlanceModifier.systemCornerRadius()) }
    }

    private companion object {
        private val PREVIEW_SIZES = setOf(
            DpSize(180.dp, 230.dp),
            DpSize(245.dp, 340.dp),
        )
    }
}

@Composable
private fun WidgetBody(
    context: Context,
    items: List<UpNextWidgetItem>,
    modifier: GlanceModifier = GlanceModifier,
) {
    WidgetTheme {
        UpNextWidgetContent(
            title = widget_up_next_name.resolve(context),
            emptyMessage = widget_empty_watchlist.resolve(context),
            items = items,
            openApp = context.openAppAction(),
            itemAction = { item -> context.openItemAction(item) },
            modifier = modifier,
        )
    }
}

private suspend fun WidgetShow.toWidgetItem(context: Context, urls: DeepLinkUrls): UpNextWidgetItem =
    UpNextWidgetItem(
        showId = tmdbId,
        showName = showName,
        episodeName = episodeName,
        seasonEpisodeLabel = context.seasonEpisodeLabel(seasonNumber, episodeNumber),
        poster = context.loadPoster(posterUrl),
        url = urls.urlFor(
            DeepLink.Episode(
                showId = tmdbId,
                seasonNumber = seasonNumber,
                episodeNumber = episodeNumber,
            ),
        ),
    )

internal fun Context.seasonEpisodeLabel(seasonNumber: Long, episodeNumber: Long): String =
    widget_season_episode.resolve(this).format(
        seasonNumber.toString().padStart(2, '0'),
        episodeNumber.toString().padStart(2, '0'),
    )

internal fun Context.openAppAction(): Action =
    actionStartActivity(packageManager.getLaunchIntentForPackage(packageName) ?: Intent())

internal fun Context.openItemAction(item: UpNextWidgetItem): Action {
    val url = item.url ?: return openAppAction()
    return actionStartActivity(Intent(Intent.ACTION_VIEW, url.toUri()).setPackage(packageName))
}

internal fun previewItems(context: Context): List<UpNextWidgetItem> = listOf(
    previewItem(context, 1396, "Breaking Bad", "Pilot"),
    previewItem(context, 60059, "Better Call Saul", "Uno"),
    previewItem(context, 1399, "Game of Thrones", "Winter Is Coming"),
    previewItem(context, 82856, "The Mandalorian", "Chapter 1"),
)

private fun previewItem(
    context: Context,
    showId: Long,
    showName: String,
    episodeName: String,
) = UpNextWidgetItem(
    showId = showId,
    showName = showName,
    episodeName = episodeName,
    seasonEpisodeLabel = context.seasonEpisodeLabel(1, 1),
    poster = null,
    url = null,
)
