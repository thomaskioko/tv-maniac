package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing

/**
 * Compact, non-intrusive error for a section that fails to load. Trakt-style: a muted line of text
 * directly under the section title with an optional inline retry link beneath it, left-aligned so
 * the title and error read as one section rather than two floating elements.
 */
@Composable
public fun InlineSectionError(
    message: String,
    modifier: Modifier = Modifier,
    retryLabel: String? = null,
    onRetry: (() -> Unit)? = null,
    retryModifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TvManiacSpacing.medium, vertical = TvManiacSpacing.xSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxSmall),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        if (retryLabel != null && onRetry != null) {
            Text(
                text = retryLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = retryModifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable(onClick = onRetry)
                    .padding(vertical = TvManiacSpacing.xxSmall),
            )
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun InlineSectionErrorPreview() {
    InlineSectionError(
        message = "Couldn't load your lists.",
        retryLabel = "Retry",
        onRetry = {},
    )
}
