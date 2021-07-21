package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity


@Composable
fun TvShowCard(
    entity: TvShowsEntity,
    isFirstCard: Boolean = false,
    modifier: Modifier = Modifier,
    imageWidth: Dp = 120.dp,
    onClick: () -> Unit
) {
    RowSpacer(value = if (isFirstCard) 16 else 4)

    Column(
        modifier = modifier
            .width(imageWidth)
            .padding(vertical = 8.dp)
    ) {
        Card(
            elevation = 4.dp,
            modifier = Modifier.clickable { onClick() },
            shape = MaterialTheme.shapes.medium
        ) {
            NetworkImageComposable(
                imageUrl = entity.posterImageUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(3 / 4F)
            )
        }

        ColumnSpacer(8)

        Text(
            text = entity.title,
            style = MaterialTheme.typography.body2,
            maxLines = 2,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        ColumnSpacer(4)

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = entity.votes.toString(),
                style = MaterialTheme.typography.overline,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
    }

    RowSpacer(value = 4)
}

