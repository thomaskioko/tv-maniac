package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun BasicDialog(
    dialogTitle: String,
    dialogMessage: String,
    confirmButtonText: String,
    onDismissDialog: () -> Unit = {},
    confirmButtonClicked: () -> Unit = {},
    dismissButtonClicked: () -> Unit = {},
    dismissButtonText: String? = null
) {

    AlertDialog(
        onDismissRequest = { onDismissDialog() },
        title = { Text(dialogTitle) },
        text = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = dialogMessage,
                    style = MaterialTheme.typography.body2,
                )
            }
        },
        confirmButton = {
            OutlinedButton(onClick = confirmButtonClicked) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = confirmButtonText,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
        },
        dismissButton = {
            dismissButtonText?.let {
                OutlinedButton(onClick = dismissButtonClicked) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = dismissButtonText,
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                }
            }
        }
    )
}
