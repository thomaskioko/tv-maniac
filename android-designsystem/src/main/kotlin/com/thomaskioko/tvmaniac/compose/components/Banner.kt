package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing

@Stable
public enum class BannerStyle {
    Error,
    Warning,
    Success,
    Info,
}

@Immutable
internal data class BannerColors(
    val container: Color,
    val content: Color,
)

@Composable
private fun BannerStyle.colors(): BannerColors = when (this) {
    BannerStyle.Error -> BannerColors(
        container = MaterialTheme.colorScheme.errorContainer,
        content = MaterialTheme.colorScheme.onErrorContainer,
    )
    BannerStyle.Warning -> BannerColors(
        container = Color(0xFFFB8C00),
        content = Color.White,
    )
    BannerStyle.Success -> BannerColors(
        container = Color(0xFF43A047),
        content = Color.White,
    )
    BannerStyle.Info -> BannerColors(
        container = Color(0xFF1E88E5),
        content = Color.White,
    )
}

/**
 * Persistent, dismissible top-of-screen banner. Renders edge-to-edge with no corner radius
 * so it reads as part of the system chrome and stays visually distinct from
 * [TvManiacSnackBar], which is rounded and inset. Applies [statusBarsPadding] internally so
 * consumers do not re-add it, and animates in/out via [AnimatedVisibility].
 *
 * Layout stacks the message row (text, close) above an optional [action] row aligned to the
 * trailing edge. The `Error` variant resolves its palette from
 * [MaterialTheme.colorScheme.errorContainer]/[androidx.compose.material3.ColorScheme.onErrorContainer]
 * so it adapts to every active theme; other variants use a fixed palette.
 *
 * @param message Body copy shown to the user.
 * @param onDismiss Invoked when the close icon is tapped.
 * @param modifier Layout modifier applied to the outer animated container.
 * @param visible Drives the enter/exit animation. Hide via `false` to slide the banner back up.
 * @param style Visual variant. Drives background and content colors.
 * @param dismissContentDescription Accessibility label for the close icon.
 * @param action Optional trailing action slot rendered below the message, right-aligned.
 */
@Composable
public fun TvManiacBanner(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    style: BannerStyle = BannerStyle.Info,
    dismissContentDescription: String? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val colors = style.colors()
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)) +
            slideInVertically(animationSpec = tween(durationMillis = 300), initialOffsetY = { -it }),
        exit = fadeOut(animationSpec = tween(durationMillis = 150)) +
            slideOutVertically(animationSpec = tween(durationMillis = 300), targetOffsetY = { -it }),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tvmaniac_banner"),
            shape = RectangleShape,
            color = colors.container,
            contentColor = colors.content,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(
                        start = TvManiacSpacing.medium,
                        end = TvManiacSpacing.xSmall,
                        top = TvManiacSpacing.small,
                        bottom = TvManiacSpacing.small,
                    ),
                verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = dismissContentDescription,
                        )
                    }
                }

                if (action != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        action()
                    }
                }
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun TvManiacBannerPreview(
    @PreviewParameter(BannerPreviewParameterProvider::class) param: BannerPreviewParam,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        TvManiacBanner(
            message = param.message,
            onDismiss = {},
            style = param.style,
            dismissContentDescription = "Dismiss",
            action = param.action,
        )
    }
}

internal data class BannerPreviewParam(
    val message: String,
    val style: BannerStyle,
    val action: (@Composable () -> Unit)? = null,
)

private class BannerPreviewParameterProvider : PreviewParameterProvider<BannerPreviewParam> {
    override val values: Sequence<BannerPreviewParam> = sequenceOf(
        BannerPreviewParam(
            message = "Your Trakt account is full. Upgrade to keep syncing new shows.",
            style = BannerStyle.Error,
            action = { Text("Upgrade") },
        ),
        BannerPreviewParam(
            message = "Your session is about to expire.",
            style = BannerStyle.Warning,
        ),
        BannerPreviewParam(
            message = "Library synced successfully.",
            style = BannerStyle.Success,
        ),
        BannerPreviewParam(
            message = "New episode notifications are now available.",
            style = BannerStyle.Info,
        ),
    )
}
