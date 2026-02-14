package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

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

@Composable
public fun TvManiacSnackBarHost(
    message: String?,
    modifier: Modifier = Modifier,
    style: SnackBarStyle = SnackBarStyle.Error,
    durationMillis: Long = 10000L,
    alignment: Alignment = Alignment.TopCenter,
    onDismiss: () -> Unit = {},
) {
    var visible by remember { mutableStateOf(false) }
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val dismissThreshold = remember(density) { with(density) { 56.dp.toPx() } }
    val flingVelocityThreshold = remember(density) { with(density) { 500.dp.toPx() } }

    fun dismiss() {
        visible = false
        currentOnDismiss()
    }

    LaunchedEffect(message) {
        offsetX.snapTo(0f)
        offsetY.snapTo(0f)
        if (message != null) {
            visible = true
            delay(durationMillis)
            dismiss()
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
            TvManiacSnackBar(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt().coerceAtMost(0)) }
                    .alpha(
                        (
                            1f - maxOf(
                                abs(offsetX.value) / (dismissThreshold * 3),
                                abs(offsetY.value) / (dismissThreshold * 3),
                            )
                            ).coerceIn(0f, 1f),
                    )
                    .pointerInput(Unit) {
                        val velocityTracker = VelocityTracker()
                        detectDragGestures(
                            onDragStart = { velocityTracker.resetTracking() },
                            onDragEnd = {
                                val velocity = velocityTracker.calculateVelocity()
                                val isFlingUp = velocity.y < -flingVelocityThreshold
                                val isFlingHorizontal = abs(velocity.x) > flingVelocityThreshold

                                if (offsetY.value < -dismissThreshold || isFlingUp) {
                                    dismiss()
                                } else if (abs(offsetX.value) > dismissThreshold || isFlingHorizontal) {
                                    dismiss()
                                } else {
                                    coroutineScope.launch { offsetX.animateTo(0f) }
                                    coroutineScope.launch { offsetY.animateTo(0f) }
                                }
                            },
                            onDragCancel = {
                                velocityTracker.resetTracking()
                                coroutineScope.launch { offsetX.animateTo(0f) }
                                coroutineScope.launch { offsetY.animateTo(0f) }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                velocityTracker.addPosition(
                                    change.uptimeMillis,
                                    change.position,
                                )
                                coroutineScope.launch { offsetX.snapTo(offsetX.value + dragAmount.x) }
                                coroutineScope.launch {
                                    offsetY.snapTo((offsetY.value + dragAmount.y).coerceAtMost(0f))
                                }
                            },
                        )
                    },
                message = message.orEmpty(),
                style = style,
            )
        }
    }
}

@Composable
internal fun TvManiacSnackBar(
    message: String,
    modifier: Modifier = Modifier,
    style: SnackBarStyle = SnackBarStyle.Error,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.large)
            .background(style.backgroundColor)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = style.icon,
            contentDescription = null,
            tint = Color.White,
        )

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
@Composable
private fun TvManiacSnackBarPreview(
    @PreviewParameter(SnackBarPreviewParameterProvider::class) param: SnackBarPreviewParam,
) {
    TvManiacTheme {
        TvManiacSnackBar(
            message = param.message,
            style = param.style,
        )
    }
}

@ThemePreviews
@Composable
private fun StandardSnackBarPreview() {
    TvManiacTheme {
        StandardSnackBar(
            snackBarHostState = SnackbarHostState(),
            errorMessage = "Somethig went wrong",
            actionLabel = "Retry",
        )
    }
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
