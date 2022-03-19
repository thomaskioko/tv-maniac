package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun ExtendedFab(
    painter: Painter,
    text: String,
    onClick: () -> Unit = {}
) {
    ExtendedFloatingActionButton(
        icon = {
            Image(
                painter = painter,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary.copy(alpha = 0.8F)),
            )
        },
        text = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.body2,
                )
            }
        },
        shape = RectangleShape,
        backgroundColor = Color.Transparent,
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
        onClick = { onClick() },
        modifier = Modifier
            .padding(2.dp)
            .border(1.dp, Color(0xFF414141), RoundedCornerShape(8.dp))
    )
}
