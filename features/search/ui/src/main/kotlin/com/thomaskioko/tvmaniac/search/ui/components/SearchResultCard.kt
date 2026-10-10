package com.thomaskioko.tvmaniac.search.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.metadataWithAccentDots
import com.thomaskioko.tvmaniac.compose.theme.ImageType
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.i18n.MR.plurals.plurals_search_episode_count
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem

@Composable
internal fun SearchResultCard(
    item: ShowItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val caption = searchResultCaption(year = item.year, episodeCount = item.episodeCount)
    val accessibilityLabel = listOfNotNull(item.title, caption?.text).joinToString(separator = ", ")
    Column {
        PosterCard(
            imageUrl = item.posterImageUrl,
            title = item.title,
            isInLibrary = item.inLibrary,
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(ImageType.Poster.aspect)
                .semantics { contentDescription = accessibilityLabel },
        )

        caption?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = TvManiacSpacing.xxSmall)
                    .clearAndSetSemantics {},
            )
        }
    }
}

@Composable
private fun searchResultCaption(year: String?, episodeCount: Int?): AnnotatedString? {
    val parts = buildList {
        year?.let { add(it) }
        episodeCount?.let { add(pluralStringResource(plurals_search_episode_count.resourceId, it, it)) }
    }
    return if (parts.isEmpty()) null else metadataWithAccentDots(parts)
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun SearchResultCardPreview() {
    SearchResultCard(
        item = ShowItem(
            showId = 84958,
            tmdbId = 84958,
            title = "Loki",
            posterImageUrl = null,
            year = "2019",
            episodeCount = 62,
            inLibrary = true,
        ),
        onClick = {},
        modifier = Modifier.width(120.dp),
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun SearchResultCardNoYearPreview() {
    SearchResultCard(
        item = ShowItem(
            showId = 94958,
            tmdbId = 94958,
            title = "Ted Lasso",
            posterImageUrl = null,
            episodeCount = 34,
            inLibrary = false,
        ),
        onClick = {},
        modifier = Modifier.width(120.dp),
    )
}
