package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme

@Composable
fun SheetDragHandle(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  title: String? = null,
) {
  Box(
    modifier =
      modifier.fillMaxWidth().statusBarsPadding().height(56.dp).background(Color.Transparent),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart).padding(start = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = Icons.Outlined.KeyboardArrowDown,
        contentDescription = "Expand/Collapse",
        modifier = Modifier.size(24.dp).clickable { onClick() },
      )

      Spacer(modifier = Modifier.width(8.dp))

      title?.let {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

@ThemePreviews
@Composable
private fun CustomSheetDragHandlePreview() {
  TvManiacTheme {
    Surface {
      SheetDragHandle(
        title = "Drag Handle",
        onClick = {},
      )
    }
  }
}
