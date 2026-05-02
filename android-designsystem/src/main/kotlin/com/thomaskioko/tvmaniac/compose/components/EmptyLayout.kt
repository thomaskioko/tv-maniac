package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider

@Composable
public fun EmptyStateView(
    title: String,
    modifier: Modifier = Modifier,
    imageVector: ImageVector = Icons.Outlined.Inbox,
    message: String? = null,
    buttonText: String? = null,
    buttonTestTag: String? = null,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            modifier = Modifier.size(64.dp),
            imageVector = imageVector,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        message?.let {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        buttonText?.let {
            Spacer(modifier = Modifier.height(24.dp))

            HorizontalOutlinedButton(
                modifier = buttonTestTag?.let { Modifier.testTag(it) } ?: Modifier,
                text = it,
                onClick = onClick,
                shape = MaterialTheme.shapes.small,
            )
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun EmptyStateViewPreview() {
    EmptyStateView(
        title = "Nothing here yet",
        message = "Shows you follow will appear here.",
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun EmptyStateViewWithButtonPreview() {
    EmptyStateView(
        title = "Something went wrong",
        message = "We couldn't load the data.",
        buttonText = "Retry",
        onClick = {},
    )
}
