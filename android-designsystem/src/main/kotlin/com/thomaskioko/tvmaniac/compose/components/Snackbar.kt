package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Stable
public enum class SnackBarStyle(
    internal val backgroundColor: Color,
    internal val icon: ImageVector,
) {
    Error(
        backgroundColor = Color(0xFFE53935),
        icon = Icons.Default.Cancel,
    ),
    Warning(
        backgroundColor = Color(0xFFFB8C00),
        icon = Icons.Default.Warning,
    ),
    Success(
        backgroundColor = Color(0xFF43A047),
        icon = Icons.Default.CheckCircle,
    ),
    Info(
        backgroundColor = Color(0xFF1E88E5),
        icon = Icons.Default.Info,
    ),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun TvManiacSnackBarHost(
    message: String?,
    modifier: Modifier = Modifier,
    style: SnackBarStyle = SnackBarStyle.Error,
    durationMillis: Long = 10000L,
    persistent: Boolean = false,
    loading: Boolean = false,
    alignment: Alignment = Alignment.TopCenter,
    onDismiss: () -> Unit = {},
) {
    var visible by remember { mutableStateOf(false) }
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    fun dismiss() {
        visible = false
        currentOnDismiss()
    }

    LaunchedEffect(message, persistent) {
        if (message != null) {
            visible = true
            if (!persistent) {
                delay(durationMillis)
                dismiss()
            }
        } else {
            visible = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.align(alignment),
            enter = slideInVertically(
                animationSpec = tween(durationMillis = 300),
                initialOffsetY = { -it },
            ),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 300),
                targetOffsetY = { -it },
            ),
        ) {
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value != SwipeToDismissBoxValue.Settled) {
                        dismiss()
                        true
                    } else {
                        false
                    }
                },
            )

            LaunchedEffect(message) {
                if (message != null) {
                    dismissState.reset()
                }
            }

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {},
                content = {
                    TvManiacSnackBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        message = message.orEmpty(),
                        style = style,
                        loading = loading,
                    )
                },
            )
        }
    }
}

@Composable
internal fun TvManiacSnackBar(
    message: String,
    modifier: Modifier = Modifier,
    style: SnackBarStyle = SnackBarStyle.Error,
    loading: Boolean = false,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.large)
            .background(style.backgroundColor)
            .padding(16.dp)
            .testTag("tvmaniac_snackbar"),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp,
            )
        } else {
            Icon(
                imageVector = style.icon,
                contentDescription = null,
                tint = Color.White,
            )
        }

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            maxLines = 3,
        )
    }
}

@Composable
public fun StandardSnackBar(
    snackBarHostState: SnackbarHostState,
    errorMessage: String?,
    actionLabel: String?,
    showError: Boolean = !errorMessage.isNullOrBlank(),
    onErrorAction: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = showError,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        errorMessage?.let {
            LaunchedEffect(errorMessage) {
                val actionResult = snackBarHostState.showSnackbar(
                    message = errorMessage,
                    actionLabel = actionLabel,
                    duration = SnackbarDuration.Long,
                )

                when (actionResult) {
                    SnackbarResult.ActionPerformed -> onErrorAction()
                    SnackbarResult.Dismissed -> onErrorAction()
                }
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun TvManiacSnackBarPreview(
    @PreviewParameter(SnackBarPreviewParameterProvider::class) param: SnackBarPreviewParam,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        TvManiacSnackBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            message = param.message,
            style = param.style,
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun StandardSnackBarPreview() {
    StandardSnackBar(
        snackBarHostState = SnackbarHostState(),
        errorMessage = "Somethig went wrong",
        actionLabel = "Retry",
    )
}

internal data class SnackBarPreviewParam(
    val message: String,
    val style: SnackBarStyle,
)

private class SnackBarPreviewParameterProvider : PreviewParameterProvider<SnackBarPreviewParam> {
    override val values: Sequence<SnackBarPreviewParam> = sequenceOf(
        SnackBarPreviewParam(
            message = "Something went wrong while syncing your data. Check your internet connection. If the problem persists, contact us.",
            style = SnackBarStyle.Error,
        ),
        SnackBarPreviewParam(
            message = "Your session is about to expire.",
            style = SnackBarStyle.Warning,
        ),
        SnackBarPreviewParam(
            message = "Changes saved successfully.",
            style = SnackBarStyle.Success,
        ),
        SnackBarPreviewParam(
            message = "Your data has been synced successfully.",
            style = SnackBarStyle.Info,
        ),
    )
}
