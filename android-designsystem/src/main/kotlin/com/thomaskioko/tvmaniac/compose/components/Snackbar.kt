package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_dismiss
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

@Stable
public enum class SnackBarStyle(
    public val backgroundColor: Color,
    public val icon: ImageVector,
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
    Syncing(
        backgroundColor = Color(0xFFCC5500),
        icon = Icons.Default.Sync,
    ),
}

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

    var displayedMessage by remember { mutableStateOf(message.orEmpty()) }
    var displayedStyle by remember { mutableStateOf(style) }
    var displayedLoading by remember { mutableStateOf(loading) }

    fun dismiss() {
        visible = false
        currentOnDismiss()
    }

    LaunchedEffect(message, persistent) {
        if (message != null) {
            displayedMessage = message
            displayedStyle = style
            displayedLoading = loading
            visible = true
            if (!persistent) {
                delay(durationMillis.milliseconds)
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
            val coroutineScope = rememberCoroutineScope()
            val density = LocalDensity.current
            var offsetY by remember { mutableStateOf(0f) }

            LaunchedEffect(message) {
                if (message != null) {
                    offsetY = 0f
                }
            }

            TvManiacSnackBar(
                onDismiss = ::dismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = TvManiacSpacing.medium)
                    .offset { IntOffset(0, offsetY.roundToInt()) }
                    .pointerInput(alignment) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { _, dragAmount ->
                                val isTop = alignment == Alignment.TopCenter ||
                                    alignment == Alignment.TopStart ||
                                    alignment == Alignment.TopEnd

                                offsetY = if (isTop) {
                                    (offsetY + dragAmount).coerceAtMost(0f)
                                } else {
                                    (offsetY + dragAmount).coerceAtLeast(0f)
                                }
                            },
                            onDragEnd = {
                                if (abs(offsetY) > with(density) { 40.dp.toPx() }) {
                                    dismiss()
                                } else {
                                    coroutineScope.launch {
                                        Animatable(offsetY).animateTo(0f) {
                                            offsetY = value
                                        }
                                    }
                                }
                            },
                        )
                    },
                message = displayedMessage,
                style = displayedStyle,
                loading = displayedLoading,
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
    onDismiss: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TvManiacSpacing.medium)
            .clip(MaterialTheme.shapes.large)
            .background(style.backgroundColor)
            .padding(TvManiacSpacing.medium)
            .testTag("tvmaniac_snackbar"),
        horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
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
                contentDescription = stringResource(cd_dismiss.resourceId),
                tint = Color.White,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onDismiss),
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
                .padding(vertical = TvManiacSpacing.medium),
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
        SnackBarPreviewParam(
            message = "Syncing your library",
            style = SnackBarStyle.Syncing,
        ),
    )
}
