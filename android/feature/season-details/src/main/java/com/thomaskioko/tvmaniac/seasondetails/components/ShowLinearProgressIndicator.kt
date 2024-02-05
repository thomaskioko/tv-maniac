package com.thomaskioko.tvmaniac.seasondetails.components

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thomaskioko.tvmaniac.compose.theme.green

@Composable
fun ShowLinearProgressIndicator(
  progress: Float,
  modifier: Modifier = Modifier,
) {
  LinearProgressIndicator(
    progress = progress,
    trackColor =
      if (progress == 1f) {
        green.copy(alpha = 0.5F)
      } else {
        MaterialTheme.colorScheme.secondary.copy(
          alpha = 0.5F,
        )
      },
    modifier = modifier,
  )
}
