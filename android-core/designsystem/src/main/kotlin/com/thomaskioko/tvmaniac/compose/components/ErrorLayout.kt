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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.SignalWifi4Bar
import androidx.compose.material.icons.outlined.SignalWifiOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.green
import com.thomaskioko.tvmaniac.resources.R

@Composable
fun ConnectionStatus(
    isConnected: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        if (isConnected) green else MaterialTheme.colorScheme.error,
        label = "",
    )
    val message = if (isConnected) {
        stringResource(id = R.string.status_connected)
    } else {
        stringResource(id = R.string.status_no_connection)
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
fun ErrorUi(
    errorMessage: String?,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    showRetryButton: Boolean = true,
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(120.dp),
                imageVector = Icons.Outlined.ErrorOutline,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary.copy(alpha = 0.8F)),
                contentDescription = null,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = errorMessage ?: stringResource(id = R.string.unexpected_error_retry),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(visible = showRetryButton) {
                TvManiacOutlinedButton(
                    text = "Retry",
                    onClick = onRetry,
                )
            }
        }
    }
}

@Composable
fun RowError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String = stringResource(id = R.string.unexpected_error_retry),
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

        TvManiacOutlinedButton(
            text = "Retry",
            onClick = onRetry,
        )
    }
}

@Composable
fun EmptyUi(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.generic_empty_content),
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(120.dp),
                imageVector = Icons.Outlined.Collections,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary.copy(alpha = 0.8F)),
                contentDescription = null,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@ThemePreviews
@Composable
fun ErrorUiPreview() {
    TvManiacTheme {
        Surface {
            ErrorUi(
                errorMessage = "Opps! Something went wrong",
                onRetry = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@ThemePreviews
@Composable
fun RowErrorPreview() {
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
