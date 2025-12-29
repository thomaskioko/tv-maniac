package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme

@Composable
public fun LoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
) {
    Box(
        modifier = modifier,
    ) {
        CircularProgressIndicator(
            color = color,
        )
    }
}

@ThemePreviews
@Composable
private fun CircularProgressIndicatorPreview() {
    TvManiacTheme { Surface { CircularProgressIndicator() } }
}
