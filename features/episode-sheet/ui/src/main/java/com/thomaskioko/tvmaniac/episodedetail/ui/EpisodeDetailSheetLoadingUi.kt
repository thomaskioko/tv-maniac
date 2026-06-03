package com.thomaskioko.tvmaniac.episodedetail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ShimmerBox
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider

@Composable
internal fun EpisodeDetailSheetLoadingUi(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            shape = RectangleShape,
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            ShimmerBox(
                modifier = Modifier
                    .width(220.dp)
                    .height(28.dp),
                shape = RoundedCornerShape(4.dp),
            )

            ShimmerBox(
                modifier = Modifier
                    .width(140.dp)
                    .height(18.dp),
                shape = RoundedCornerShape(4.dp),
            )

            ShimmerBox(
                modifier = Modifier
                    .width(90.dp)
                    .height(16.dp),
                shape = RoundedCornerShape(4.dp),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp),
                    shape = RoundedCornerShape(4.dp),
                )
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp),
                    shape = RoundedCornerShape(4.dp),
                )
                ShimmerBox(
                    modifier = Modifier
                        .width(200.dp)
                        .height(14.dp),
                    shape = RoundedCornerShape(4.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
        )

        Spacer(modifier = Modifier.height(8.dp))

        repeat(3) {
            EpisodeDetailLoadingActionRow()
        }
    }
}

@Composable
private fun EpisodeDetailLoadingActionRow(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ShimmerBox(
            modifier = Modifier.size(24.dp),
            shape = RoundedCornerShape(8.dp),
        )
        ShimmerBox(
            modifier = Modifier
                .width(160.dp)
                .height(18.dp),
            shape = RoundedCornerShape(4.dp),
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun EpisodeDetailSheetLoadingUiPreview() {
    EpisodeDetailSheetLoadingUi()
}
