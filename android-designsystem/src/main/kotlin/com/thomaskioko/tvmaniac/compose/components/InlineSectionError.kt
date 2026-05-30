package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp


@Composable
public fun InlineSectionError(
    message: String,
    modifier: Modifier = Modifier,
    retryLabel: String? = null,
    onRetry: (() -> Unit)? = null,
    retryModifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        if (retryLabel != null && onRetry != null) {
            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = retryLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = retryModifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable(onClick = onRetry)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
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
