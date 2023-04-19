package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.resources.R

@Composable
fun TvPosterCard(
    posterImageUrl: String?,
    title: String,
    modifier: Modifier = Modifier,
    imageWidth: Dp = 120.dp,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .width(imageWidth)
            .clickable { onClick() },
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Box {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.Center),
            )

            AsyncImageComposable(
                model = posterImageUrl,
                contentDescription = stringResource(R.string.cd_show_poster, title),
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(2 / 3f),
            )
        }
    }
}
