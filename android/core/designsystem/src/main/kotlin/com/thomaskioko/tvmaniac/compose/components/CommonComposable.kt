package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.SignalWifi4Bar
import androidx.compose.material.icons.outlined.SignalWifiOff
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thomaskioko.tvmaniac.compose.theme.colorError
import com.thomaskioko.tvmaniac.compose.theme.green
import com.thomaskioko.tvmaniac.compose.theme.grey600
import com.thomaskioko.tvmaniac.resources.R

@Composable
fun ErrorView(
    message: String? = null
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(120.dp),
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = MaterialTheme.colors.secondary
        )
        ColumnSpacer(value = 12)

        Text(
            text = message ?: stringResource(id = R.string.generic_error_message),
            style = MaterialTheme.typography.overline,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

@Composable
fun EmptyContentView(
    painter: Painter,
    message: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painter,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface.copy(alpha = 0.8F)),
            modifier = Modifier.size(96.dp),
            contentDescription = null
        )

        ColumnSpacer(value = 12)

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = message,
                style = MaterialTheme.typography.body2,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
            )
        }

        //TODO:: Add Retry
    }
}

@Composable
fun CircularLoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.secondary
        )
    }
}

/**
 * Full screen circular progress indicator
 */
@Composable
fun FullScreenLoading(
    isVisible: Boolean = true
) {
    AnimatedVisibility(
        visible = isVisible,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.secondary
            )
        }
    }
}

@Composable
fun LoadingItem() {
    CircularProgressIndicator(
        color = MaterialTheme.colors.secondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}

@Composable
fun SnackBarErrorRetry(
    snackBarHostState: SnackbarHostState,
    errorMessage: String?,
    actionLabel: String?,
    showError: Boolean = !errorMessage.isNullOrBlank(),
    onErrorAction: () -> Unit = { },
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
                    actionLabel = actionLabel
                )

                when (actionResult) {
                    SnackbarResult.ActionPerformed -> onErrorAction()
                    SnackbarResult.Dismissed -> onErrorAction()
                }
            }
        }
    }
}

@Composable
fun ConnectionStatus(isConnected: Boolean) {
    val backgroundColor by animateColorAsState(if (isConnected) green else colorError)
    val message = if (isConnected) stringResource(id = R.string.status_connected)
    else stringResource(id = R.string.status_no_connection)
    val icon = if (isConnected) Icons.Outlined.SignalWifi4Bar else Icons.Outlined.SignalWifiOff

    Box(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(10.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = "Connectivity Icon",
                tint = Color.White
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                message,
                color = Color.White,
                style = MaterialTheme.typography.caption,
            )
        }
    }
}

@Composable
fun ErrorUi(
    modifier: Modifier = Modifier.size(120.dp),
    errorMessage: String = stringResource(R.string.unexpected_error_retry),
    onRetry: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                imageVector = Icons.Outlined.WarningAmber,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface.copy(alpha = 0.8F)),
                modifier = modifier,
                contentDescription = null
            )

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )

            ColumnSpacer(value = 8)

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colors.onBackground,
                        backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.08f)
                    ),
                    border= BorderStroke(1.dp, grey600),
                    onClick = { onRetry() }
                ) {
                    Text(
                        text = "Retry",
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }

    }
}

@Composable
fun RowError(
    onRetry: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                imageVector = Icons.Outlined.WarningAmber,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface.copy(alpha = 0.8F)),
                modifier = Modifier.size(24.dp),
                contentDescription = null
            )

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colors.onBackground,
                        backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.08f)
                    ),
                    border = BorderStroke(1.dp, grey600),
                    onClick = { onRetry() }
                ) {
                    Text(
                        text = "Retry",
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingRowContent(
    isLoading: Boolean,
    text: String,
    content: @Composable () -> Unit,
) {
    ColumnSpacer(8)

    Column {
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
        ) {

            Text(
                text = text,
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )

            this@Column.AnimatedVisibility(
                visible = isLoading,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(32.dp),
                    color = MaterialTheme.colors.secondary
                )
            }
        }
    }

    ColumnSpacer(4)

    content()

}

@Preview
@Composable
fun ErrorUiPreview() {
    Surface(Modifier.fillMaxWidth()) {
        ErrorUi(
            errorMessage = "Opps",
            onRetry = {}
        )
    }
}

@Preview
@Composable
fun RowErrorPreview() {
    Surface(Modifier.fillMaxWidth()) {
        RowError(
            onRetry = {}
        )
    }
}
