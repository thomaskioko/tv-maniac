package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.android.designsystem.R

@Composable
public fun ProviderSignInCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    showBackground: Boolean = true,
    buttons: @Composable ColumnScope.() -> Unit,
) {
    if (showBackground) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            ),
        ) {
            ProviderSignInContent(
                title = title,
                description = description,
                modifier = Modifier.padding(16.dp),
                buttons = buttons,
            )
        }
    } else {
        ProviderSignInContent(
            title = title,
            description = description,
            modifier = modifier,
            buttons = buttons,
        )
    }
}

@Composable
private fun ProviderSignInContent(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    buttons: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = buttons,
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ProviderSignInCardPreview() {
    Box(Modifier.padding(16.dp)) {
        ProviderSignInCard(
            title = "Connect & Sync Your Content",
            description = "Save your progress, discover new titles, and sync your content across all devices.",
        ) {
            ProviderButton(
                text = "Continue with Trakt",
                logo = R.drawable.ic_trakt_mono,
                onClick = {},
            )
            ProviderButton(
                text = "Continue with Simkl",
                logo = R.drawable.ic_simkl_mono,
                onClick = {},
            )
        }
    }
}
