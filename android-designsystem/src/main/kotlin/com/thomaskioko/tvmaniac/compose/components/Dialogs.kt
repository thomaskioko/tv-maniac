package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider

@Composable
public fun TvManiacAlertDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    icon: ImageVector? = null,
    dismissButtonText: String? = null,
    confirmButtonTestTag: String? = null,
    dismissButtonTestTag: String? = null,
) {
    val density = LocalDensity.current
    val containerWidth = with(density) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = (containerWidth - 80.dp).coerceAtLeast(0.dp)),
        shape = shape,
        onDismissRequest = onDismiss,
        icon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        confirmButton = {
            TextButton(
                modifier = confirmButtonTestTag?.let { Modifier.testTag(it) } ?: Modifier,
                onClick = onConfirm,
            ) {
                Text(
                    text = confirmButtonText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        },
        dismissButton = dismissButtonText?.let {
            {
                TextButton(
                    modifier = dismissButtonTestTag?.let { tag -> Modifier.testTag(tag) } ?: Modifier,
                    onClick = onDismiss,
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun TvManiacAlertDialogPreview() {
    TvManiacAlertDialog(
        title = "Enable Notifications",
        message = "Get notified when new episodes of your favorite shows are released.",
        confirmButtonText = "Enable",
        dismissButtonText = "Not Now",
        icon = Icons.Default.Info,
        onConfirm = {},
        onDismiss = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun TvManiacAlertDialogNoIconPreview() {
    TvManiacAlertDialog(
        title = "Confirm Action",
        message = "Are you sure you want to proceed with this action?",
        confirmButtonText = "Confirm",
        dismissButtonText = "Cancel",
        onConfirm = {},
        onDismiss = {},
    )
}
