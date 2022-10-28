package com.thomaskioko.tvmaniac.compose.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.resources.R

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

@Composable
fun ExtendedLoadingFab(
    isLoading: Boolean,
    painter: Painter,
    text: String,
    onClick: () -> Unit = {}
) {
    ExtendedFloatingActionButton(
        icon = {
            if (isLoading) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colors.secondary,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Image(
                    painter = painter,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary.copy(alpha = 0.8F)),
                )
            }
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
            .clickable(
                enabled = isLoading,
                onClick = {}
            )
    )
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ExtendedLoadingFabPreview() {
    TvManiacTheme {
        Surface {
            ExtendedLoadingFab(
                isLoading = true,
                painter = painterResource(id = R.drawable.ic_baseline_check_box_24),
                text = "Following",
            )
        }
    }
}

