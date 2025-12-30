package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.generic_empty_content
import com.thomaskioko.tvmaniac.i18n.resolve

@Composable
public fun EmptyContent(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    message: String? = null,
    buttonText: String? = null,
    title: String = generic_empty_content.resolve(LocalContext.current),
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            modifier = Modifier.size(180.dp),
            imageVector = imageVector,
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8F),
            contentDescription = null,
        )

        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        message?.let {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }

        buttonText?.let {
            HorizontalOutlinedButton(
                modifier = Modifier.padding(top = 16.dp),
                text = buttonText,
                onClick = onClick,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun EmptyContentViewPreview() {
    TvManiacTheme {
        Surface {
            EmptyContent(
                imageVector = Icons.Outlined.Inbox,
                message = generic_empty_content.resolve(LocalContext.current),
            )
        }
    }
}
