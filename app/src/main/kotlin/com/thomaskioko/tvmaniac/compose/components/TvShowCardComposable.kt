package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.R
import com.thomaskioko.tvmaniac.presentation.model.TvShow


@Composable
fun TvShowCard(
    tvShow: TvShow,
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
                imageUrl = tvShow.posterImageUrl,
                contentDescription = stringResource(R.string.cd_show_poster, tvShow.title),
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(2 / 3f)
                    .clip(MaterialTheme.shapes.medium),
            )
        }

        Spacer(Modifier.height(8.dp))
    }

    RowSpacer(value = 4)
}

