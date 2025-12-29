package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
public fun SnackBarErrorRetry(
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
                )

                when (actionResult) {
                    SnackbarResult.ActionPerformed -> onErrorAction()
                    SnackbarResult.Dismissed -> onErrorAction()
                }
            }
        }
    }
}
