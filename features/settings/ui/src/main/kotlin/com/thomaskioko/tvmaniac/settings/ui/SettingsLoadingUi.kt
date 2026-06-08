package com.thomaskioko.tvmaniac.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ShimmerBox
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider

@Composable
internal fun SettingsLoadingUi(
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        listOf(3, 4, 2).forEach { rowCount ->
            item {
                ShimmerBox(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .width(96.dp)
                        .height(14.dp),
                    shape = RoundedCornerShape(4.dp),
                )
            }

            item {
                SettingsGroup {
                    repeat(rowCount) { index ->
                        SettingsLoadingRow()
                        if (index != rowCount - 1) {
                            SettingsGroupDivider()
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun SettingsLoadingRow(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerBox(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(10.dp),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            ShimmerBox(
                modifier = Modifier
                    .width(150.dp)
                    .height(16.dp),
                shape = RoundedCornerShape(4.dp),
            )
            ShimmerBox(
                modifier = Modifier
                    .width(210.dp)
                    .height(12.dp),
                shape = RoundedCornerShape(4.dp),
            )
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun SettingsLoadingUiPreview() {
    SettingsLoadingUi()
}
