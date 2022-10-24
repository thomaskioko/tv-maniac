package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.resources.R

@Composable
fun TvShowCard(
    modifier: Modifier = Modifier,
    posterImageUrl: String?,
    title: String,
    isFirstCard: Boolean = false,
    imageWidth: Dp = 120.dp,
    rowSpacer: Int = 4,
    onClick: () -> Unit = {}
) {
    RowSpacer(value = if (isFirstCard) 16 else 4)

    Column(
        modifier = modifier
            .width(imageWidth)
            .padding(vertical = 8.dp)
    ) {
        Card(
            elevation = 4.dp,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .clickable { onClick() }

        ) {
            Box(
                modifier = Modifier.clickable(onClick = onClick)
            ) {

                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.Center)
                    )
                }

                AsyncImageComposable(
                    model = posterImageUrl,
                    contentDescription = stringResource(R.string.cd_show_poster, title),
                    modifier = Modifier
                        .aspectRatio(2 / 3f)
                        .clip(MaterialTheme.shapes.medium),
                )
            }
        }

        Spacer(Modifier.height(8.dp))
    }

    RowSpacer(value = rowSpacer)
}


@Composable
fun PosterImage(
    modifier: Modifier = Modifier,
    posterImageUrl: String?,
    title: String,
    isFirstCard: Boolean = false,
    posterModifier: Modifier = Modifier,
) {
    RowSpacer(value = if (isFirstCard) 16 else 4)

    Card(modifier = modifier) {

        Box {

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.Center)
                )
            }

            AsyncImageComposable(
                model = posterImageUrl,
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.cd_show_poster, title),
                modifier = posterModifier
            )
        }

    }

}
