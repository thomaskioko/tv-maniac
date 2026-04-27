package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme

@Composable
public fun PosterPlaceholder(
    modifier: Modifier = Modifier,
    imageSize: Dp = 52.dp,
    title: String? = null,
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Gray.copy(alpha = 0.8f),
                        Color.Gray,
                    ),
                ),
            ),
    ) {
        val (icon, text) = createRefs()

        Icon(
            modifier = Modifier
                .size(imageSize)
                .constrainAs(icon) {
                    centerTo(parent)
                },
            imageVector = Icons.Outlined.Movie,
            contentDescription = title,
            tint = Color.White.copy(alpha = 0.8f),
        )

        title?.let {
            Text(
                text = it,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .constrainAs(text) {
                        top.linkTo(icon.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun PosterPlaceholderPreview() {
    TvManiacTheme {
        Surface {
            PosterPlaceholder(
                title = "Loki",
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(2 / 3f),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun PosterPlaceholderNoTitlePreview() {
    TvManiacTheme {
        Surface {
            PosterPlaceholder(
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(2 / 3f),
            )
        }
    }
}
