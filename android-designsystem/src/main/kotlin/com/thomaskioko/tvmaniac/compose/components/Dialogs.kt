package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme

@Composable
public fun BasicDialog(
    dialogTitle: String,
    dialogMessage: String,
    confirmButtonText: String,
    enableConfirmButton: Boolean = true,
    enableDismissButton: Boolean = true,
    dismissButtonText: String? = null,
    shape: Shape = MaterialTheme.shapes.small,
    onDismissDialog: () -> Unit = {},
    confirmButtonClicked: () -> Unit = {},
    dismissButtonClicked: () -> Unit = {},
) {
    val configuration = LocalConfiguration.current

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        shape = shape,
        onDismissRequest = { onDismissDialog() },
        title = {
            Text(
                text = dialogTitle,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            HorizontalDivider()

            Text(
                text = dialogMessage,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp),
            )
        },
        confirmButton = {
            HorizontalOutlinedButton(
                enabled = enableConfirmButton,
                text = confirmButtonText,
                onClick = confirmButtonClicked,
            )
        },
        dismissButton = {
            dismissButtonText?.let {
                HorizontalOutlinedButton(
                    enabled = enableDismissButton,
                    text = dismissButtonText,
                    onClick = dismissButtonClicked,
                )
            }
        },
    )
}

@ThemePreviews
@Composable
private fun BasicDialogPreview() {
    TvManiacTheme {
        Surface {
            BasicDialog(
                dialogTitle = "Dialog Title",
                dialogMessage = "Trakt is a platform that does many things, but primarily keeps " +
                    "track of TV shows and movies you watch.",
                confirmButtonText = "Confirm",
                dismissButtonText = "Cancel",
            )
        }
    }
}
