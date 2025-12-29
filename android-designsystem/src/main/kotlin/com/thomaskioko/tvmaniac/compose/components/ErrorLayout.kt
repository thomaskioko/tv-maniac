package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.SignalWifi4Bar
import androidx.compose.material.icons.outlined.SignalWifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.green
import com.thomaskioko.tvmaniac.i18n.MR.strings.status_connected
import com.thomaskioko.tvmaniac.i18n.MR.strings.status_no_connection
import com.thomaskioko.tvmaniac.i18n.MR.strings.unexpected_error_retry
import com.thomaskioko.tvmaniac.i18n.resolve

@Composable
public fun ConnectionStatus(
    isConnected: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by
        animateColorAsState(
            if (isConnected) green else MaterialTheme.colorScheme.error,
            label = "",
        )
    val message = if (isConnected) {
        status_connected.resolve(LocalContext.current)
    } else {
        status_no_connection.resolve(LocalContext.current)
    }
    val icon = if (isConnected) Icons.Outlined.SignalWifi4Bar else Icons.Outlined.SignalWifiOff

    Box(
        modifier = modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Connectivity Icon",
                tint = Color.White,
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                message,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
public fun ErrorUi(
    errorMessage: String?,
    modifier: Modifier = Modifier,
    errorIcon: @Composable () -> Unit = {},
    onRetry: () -> Unit = {},
    showRetryButton: Boolean = true,
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            errorIcon()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = errorMessage ?: unexpected_error_retry.resolve(LocalContext.current),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(visible = showRetryButton) {
                HorizontalOutlinedButton(
                    text = "Retry",
                    onClick = onRetry,
                )
            }
        }
    }
}

@Composable
public fun RowError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String = unexpected_error_retry.resolve(LocalContext.current),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalOutlinedButton(
            text = "Retry",
            onClick = onRetry,
        )
    }
}

@Composable
public fun EmptyScreen(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            icon()

            Spacer(modifier = Modifier.height(8.dp))

            text()

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@ThemePreviews
@Composable
private fun ErrorUiPreview() {
    TvManiacTheme {
        Surface {
            ErrorUi(
                errorIcon = {
                    Image(
                        modifier = Modifier.size(120.dp),
                        imageVector = Icons.Outlined.ErrorOutline,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary.copy(alpha = 0.8F)),
                        contentDescription = null,
                    )
                },
                errorMessage = "Opps! Something went wrong",
                onRetry = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun RowErrorPreview() {
    TvManiacTheme {
        Surface {
            RowError(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                onRetry = {},
            )
        }
    }
}
