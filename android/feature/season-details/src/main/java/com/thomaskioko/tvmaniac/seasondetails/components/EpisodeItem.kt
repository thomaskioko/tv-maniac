package com.thomaskioko.tvmaniac.seasondetails.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.seasondetails.UpdateEpisodeStatus
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.seasondetails.episodeDetailsModel

@Composable
fun EpisodeItem(
  imageUrl: String?,
  title: String,
  episodeOverview: String,
  onAction: (UpdateEpisodeStatus) -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = MaterialTheme.shapes.small,
  onEpisodeClicked: () -> Unit = {},
) {
  Card(
    shape = shape,
    modifier = modifier.clickable { onEpisodeClicked() },
  ) {
    Row(
      modifier = Modifier.fillMaxSize(),
    ) {
      AsyncImageComposable(
        model = imageUrl,
        contentDescription =
          stringResource(
            R.string.cd_show_poster,
            title,
          ),
        contentScale = ContentScale.Crop,
        modifier = Modifier.width(100.dp).aspectRatio(0.8f),
      )

      Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp),
      ) {
        Text(
          text = title,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(vertical = 4.dp),
        )

        Text(
          text = episodeOverview,
          maxLines = 4,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier,
        )
      }
    }
  }
}

@ThemePreviews
@Composable
private fun WatchlistRowItemPreview() {
  TvManiacTheme {
    Surface {
      EpisodeItem(
        title = episodeDetailsModel.episodeNumberTitle,
        episodeOverview = episodeDetailsModel.overview,
        imageUrl = episodeDetailsModel.imageUrl,
        onAction = {},
      )
    }
  }
}
