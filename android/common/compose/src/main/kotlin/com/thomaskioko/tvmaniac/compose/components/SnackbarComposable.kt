package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable

/**
 * Wrapper around [Snackbar] to make it swipe-dismissable,
 * using [SwipeToDismiss].
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeDismissSnackbar(
    data: SnackbarData,
    onDismiss: (() -> Unit)? = null,
    snackbar: @Composable (SnackbarData) -> Unit = { Snackbar(it) }
) {
    val dismissState = rememberDismissState {
        if (it != DismissValue.Default) {
            // First dismiss the snackbar
            data.dismiss()
            // Then invoke the callback
            onDismiss?.invoke()
        }
        true
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {},
        dismissContent = { snackbar(data) }
    )
}
